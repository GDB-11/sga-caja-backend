package com.cibertec.sga.accountreceivable.application;

import com.cibertec.sga.accountreceivable.domain.error.AccountReceivableError;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivableMovement;
import com.cibertec.sga.accountreceivable.domain.model.MemberRef;
import com.cibertec.sga.accountreceivable.domain.model.StallRef;
import com.cibertec.sga.accountreceivable.domain.repository.IAccountReceivableRepository;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.accountreceivablestatus.domain.repository.IAccountReceivableStatusRepository;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.member.domain.model.Member;
import com.cibertec.sga.member.domain.repository.IMemberRepository;
import com.cibertec.sga.service.domain.model.Service;
import com.cibertec.sga.service.domain.repository.IServiceRepository;
import com.cibertec.sga.stall.domain.model.Stall;
import com.cibertec.sga.stall.domain.repository.IStallRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class AccountReceivableService implements IAccountReceivableService {

    private static final String CHARGE_TARGET_STALL = "Stall";
    private static final String CHARGE_TARGET_MEMBER = "Member";
    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_EXEMPT = "Exempt";

    private final IAccountReceivableRepository accountReceivableRepository;
    private final IServiceRepository serviceRepository;
    private final IStallRepository stallRepository;
    private final IMemberRepository memberRepository;
    private final IAccountReceivableStatusRepository accountReceivableStatusRepository;

    public AccountReceivableService(
        IAccountReceivableRepository accountReceivableRepository,
        IServiceRepository serviceRepository,
        IStallRepository stallRepository,
        IMemberRepository memberRepository,
        IAccountReceivableStatusRepository accountReceivableStatusRepository
    ) {
        this.accountReceivableRepository = accountReceivableRepository;
        this.serviceRepository = serviceRepository;
        this.stallRepository = stallRepository;
        this.memberRepository = memberRepository;
        this.accountReceivableStatusRepository = accountReceivableStatusRepository;
    }

    @Override
    public Page<AccountReceivable> search(UUID serviceUuid, UUID memberUuid, UUID stallUuid, Pageable pageable) {
        return accountReceivableRepository.search(serviceUuid, memberUuid, stallUuid, pageable);
    }

    @Override
    public Result<AccountReceivable, AccountReceivableError> findByUuid(UUID uuid) {
        return accountReceivableRepository.findByUuid(uuid)
            .map(Result::<AccountReceivable, AccountReceivableError>success)
            .orElseGet(() -> Result.failure(new AccountReceivableError.NotFound(uuid.toString())));
    }

    @Override
    public Result<List<AccountReceivable>, AccountReceivableError> generateByStall(GenerateByStallCommand command) {
        Result<GenerationContext, AccountReceivableError> context = validateGeneration(
            command.serviceUuid(), command.periodStartDate(), command.periodEndDate(), command.amount(), CHARGE_TARGET_STALL
        );
        if (context.isFailure()) {
            return Result.failure(context.getError());
        }

        Service service = context.getValue().service();
        BigDecimal amount = context.getValue().amount();
        AccountReceivableStatus pendingStatus = context.getValue().pendingStatus();

        List<AccountReceivable> toCreate = stallRepository.findAllActive().stream()
            .map(stall -> buildForStall(service, stall, command.periodStartDate(), command.periodEndDate(), amount, pendingStatus))
            .toList();

        return Result.success(accountReceivableRepository.insertAll(toCreate));
    }

    @Override
    public Result<List<AccountReceivable>, AccountReceivableError> generateByMember(GenerateByMemberCommand command) {
        if (command.stageCodes() == null || command.stageCodes().isEmpty()) {
            return Result.failure(new AccountReceivableError.InvalidStageFilter());
        }

        Result<GenerationContext, AccountReceivableError> context = validateGeneration(
            command.serviceUuid(), command.periodStartDate(), command.periodEndDate(), command.amount(), CHARGE_TARGET_MEMBER
        );
        if (context.isFailure()) {
            return Result.failure(context.getError());
        }

        Service service = context.getValue().service();
        BigDecimal amount = context.getValue().amount();
        AccountReceivableStatus pendingStatus = context.getValue().pendingStatus();

        List<Member> members = memberRepository.findAllActiveByStageCodes(command.stageCodes());
        if (command.uniqueMembers()) {
            members = dedupeByFullName(members);
        }

        List<AccountReceivable> toCreate = members.stream()
            .map(member -> buildForMember(service, member, command.periodStartDate(), command.periodEndDate(), amount, pendingStatus))
            .toList();

        return Result.success(accountReceivableRepository.insertAll(toCreate));
    }

    private Result<GenerationContext, AccountReceivableError> validateGeneration(
        UUID serviceUuid, LocalDate periodStartDate, LocalDate periodEndDate, BigDecimal amount, String expectedChargeTarget
    ) {
        if (periodStartDate == null || periodEndDate == null || periodEndDate.isBefore(periodStartDate)) {
            return Result.failure(new AccountReceivableError.InvalidPeriod());
        }

        var serviceOpt = serviceRepository.findByUuid(serviceUuid);
        if (serviceOpt.isEmpty()) {
            return Result.failure(new AccountReceivableError.ServiceNotFound(serviceUuid.toString()));
        }
        Service service = serviceOpt.get();

        if (!service.isActive()) {
            return Result.failure(new AccountReceivableError.ServiceInactive(serviceUuid.toString()));
        }

        if (!service.getChargeTargetType().getName().equals(expectedChargeTarget)) {
            String expectedLabel = expectedChargeTarget.equals(CHARGE_TARGET_STALL) ? "puestos" : "socios";
            return Result.failure(new AccountReceivableError.WrongChargeTarget(service.getName(), expectedLabel));
        }

        BigDecimal resolvedAmount;
        if (service.isConsumptionBased()) {
            if (amount != null) {
                return Result.failure(new AccountReceivableError.InvalidAmount(
                    "Un servicio por consumo no debe indicar monto; se calcula al registrar las lecturas (RF-17)"
                ));
            }
            resolvedAmount = BigDecimal.ZERO;
        } else {
            if (amount == null || amount.signum() <= 0) {
                return Result.failure(new AccountReceivableError.InvalidAmount(
                    "El monto es obligatorio y debe ser mayor a cero para un servicio de costo fijo"
                ));
            }
            resolvedAmount = amount;
        }

        AccountReceivableStatus pendingStatus = accountReceivableStatusRepository.findByName(STATUS_PENDING).orElseThrow();

        return Result.success(new GenerationContext(service, resolvedAmount, pendingStatus));
    }

    private List<Member> dedupeByFullName(List<Member> members) {
        Map<String, Member> byFullName = new LinkedHashMap<>();
        for (Member member : members) {
            String key = (member.getFirstName() + " " + member.getLastName()).toLowerCase(Locale.ROOT);
            byFullName.putIfAbsent(key, member);
        }
        return List.copyOf(byFullName.values());
    }

    private AccountReceivable buildForStall(
        Service service, Stall stall, LocalDate periodStartDate, LocalDate periodEndDate,
        BigDecimal amount, AccountReceivableStatus status
    ) {
        return AccountReceivable.builder()
            .service(service)
            .stall(new StallRef(stall.getUuid(), stall.getNumber()))
            .periodStartDate(periodStartDate)
            .periodEndDate(periodEndDate)
            .amount(amount)
            .status(status)
            .build();
    }

    private AccountReceivable buildForMember(
        Service service, Member member, LocalDate periodStartDate, LocalDate periodEndDate,
        BigDecimal amount, AccountReceivableStatus status
    ) {
        return AccountReceivable.builder()
            .service(service)
            .member(new MemberRef(member.getUuid(), member.getFirstName() + " " + member.getLastName()))
            .periodStartDate(periodStartDate)
            .periodEndDate(periodEndDate)
            .amount(amount)
            .status(status)
            .build();
    }

    @Override
    @Transactional
    public Result<AccountReceivable, AccountReceivableError> markExempt(UUID uuid) {
        var accountReceivableOpt = accountReceivableRepository.findByUuidForUpdate(uuid);
        if (accountReceivableOpt.isEmpty()) {
            return Result.failure(new AccountReceivableError.NotFound(uuid.toString()));
        }
        if (!accountReceivableOpt.get().getStatus().getName().equals(STATUS_PENDING)) {
            return Result.failure(new AccountReceivableError.NotPending(uuid.toString()));
        }

        AccountReceivableStatus exemptStatus = accountReceivableStatusRepository.findByName(STATUS_EXEMPT).orElseThrow();
        return Result.success(accountReceivableRepository.updateStatus(uuid, exemptStatus));
    }

    @Override
    public Result<List<AccountReceivableMovement>, AccountReceivableError> summary(UUID memberUuid, UUID stallUuid) {
        if ((memberUuid == null) == (stallUuid == null)) {
            return Result.failure(new AccountReceivableError.InvalidSummaryTarget());
        }

        if (memberUuid != null) {
            if (memberRepository.findByUuid(memberUuid).isEmpty()) {
                return Result.failure(new AccountReceivableError.TargetNotFound(memberUuid.toString()));
            }
            return Result.success(accountReceivableRepository.findMovementsByMember(memberUuid));
        }

        if (stallRepository.findByUuid(stallUuid).isEmpty()) {
            return Result.failure(new AccountReceivableError.TargetNotFound(stallUuid.toString()));
        }
        return Result.success(accountReceivableRepository.findMovementsByStall(stallUuid));
    }

    private record GenerationContext(Service service, BigDecimal amount, AccountReceivableStatus pendingStatus) {
    }
}
