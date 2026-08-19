package com.cibertec.sga.accountreceivable.infrastructure.persistence;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivableMovement;
import com.cibertec.sga.accountreceivable.domain.model.MemberRef;
import com.cibertec.sga.accountreceivable.domain.model.StallRef;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.accountreceivablestatus.infrastructure.persistence.AccountReceivableStatusJpaRepository;
import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.infrastructure.persistence.CurrencyJpaRepository;
import com.cibertec.sga.member.infrastructure.persistence.MemberJpaRepository;
import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import com.cibertec.sga.service.domain.model.Service;
import com.cibertec.sga.service.infrastructure.persistence.ServiceJpaRepository;
import com.cibertec.sga.stall.infrastructure.persistence.StallJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link AccountReceivableEntity}/{@link AccountReceivableRow} (persistencia) y
 * {@link AccountReceivable} (modelo de dominio). Resuelve {@code ServiceId}/{@code MemberId}/
 * {@code StallId}/{@code AccountReceivableStatusId} a partir de los {@code Uuid} del modelo de
 * dominio vía los {@code JpaRepository} de esos módulos (dependencia infra-a-infra) — el modelo
 * de dominio nunca conoce Ids internos.
 */
@Component
public class AccountReceivableMapper {

    private final ServiceJpaRepository serviceJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final StallJpaRepository stallJpaRepository;
    private final AccountReceivableStatusJpaRepository accountReceivableStatusJpaRepository;
    private final CurrencyJpaRepository currencyJpaRepository;

    public AccountReceivableMapper(
        ServiceJpaRepository serviceJpaRepository,
        MemberJpaRepository memberJpaRepository,
        StallJpaRepository stallJpaRepository,
        AccountReceivableStatusJpaRepository accountReceivableStatusJpaRepository,
        CurrencyJpaRepository currencyJpaRepository
    ) {
        this.serviceJpaRepository = serviceJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.stallJpaRepository = stallJpaRepository;
        this.accountReceivableStatusJpaRepository = accountReceivableStatusJpaRepository;
        this.currencyJpaRepository = currencyJpaRepository;
    }

    public AccountReceivable toDomain(AccountReceivableRow row) {
        RecurrenceType recurrenceType = RecurrenceType.builder()
            .uuid(row.getRecurrenceTypeUuid())
            .name(row.getRecurrenceTypeName())
            .build();
        ChargeTargetType chargeTargetType = ChargeTargetType.builder()
            .uuid(row.getChargeTargetTypeUuid())
            .name(row.getChargeTargetTypeName())
            .build();
        Currency serviceCurrency = Currency.builder()
            .uuid(row.getServiceCurrencyUuid())
            .code(row.getServiceCurrencyCode())
            .name(row.getServiceCurrencyName())
            .build();
        Service service = Service.builder()
            .uuid(row.getServiceUuid())
            .name(row.getServiceName())
            .recurrenceType(recurrenceType)
            .chargeTargetType(chargeTargetType)
            .currency(serviceCurrency)
            .consumptionBased(row.getServiceIsConsumptionBased())
            .cost(row.getServiceCost())
            .unitCost(row.getServiceUnitCost())
            .active(row.getServiceIsActive())
            .build();

        MemberRef member = row.getMemberUuid() == null ? null : new MemberRef(row.getMemberUuid(), row.getMemberFullName());
        StallRef stall = row.getStallUuid() == null ? null : new StallRef(row.getStallUuid(), row.getStallNumber());

        AccountReceivableStatus status = AccountReceivableStatus.builder()
            .uuid(row.getStatusUuid())
            .name(row.getStatusName())
            .build();

        Currency currency = Currency.builder()
            .uuid(row.getCurrencyUuid())
            .code(row.getCurrencyCode())
            .name(row.getCurrencyName())
            .build();

        return AccountReceivable.builder()
            .uuid(row.getUuid())
            .service(service)
            .member(member)
            .stall(stall)
            .periodStartDate(row.getPeriodStartDate())
            .periodEndDate(row.getPeriodEndDate())
            .amount(row.getAmount())
            .status(status)
            .currency(currency)
            .build();
    }

    public AccountReceivableEntity toNewEntity(AccountReceivable accountReceivable) {
        return AccountReceivableEntity.builder()
            .serviceId(resolveServiceId(accountReceivable))
            .memberId(resolveMemberId(accountReceivable))
            .stallId(resolveStallId(accountReceivable))
            .periodStartDate(accountReceivable.getPeriodStartDate())
            .periodEndDate(accountReceivable.getPeriodEndDate())
            .amount(accountReceivable.getAmount())
            .accountReceivableStatusId(resolveStatusId(accountReceivable))
            .currencyId(resolveCurrencyId(accountReceivable))
            .build();
    }

    private Long resolveCurrencyId(AccountReceivable accountReceivable) {
        return currencyJpaRepository.findByUuid(accountReceivable.getCurrency().getUuid()).orElseThrow().getId();
    }

    public AccountReceivableMovement toMovement(AccountReceivableSummaryRow row) {
        return new AccountReceivableMovement(
            toDomain(row), row.getSettlementMethod(), row.getSettledDate(), row.getReceiptCorrelative()
        );
    }

    private Long resolveServiceId(AccountReceivable accountReceivable) {
        return serviceJpaRepository.findEntityByUuid(accountReceivable.getService().getUuid()).orElseThrow().getId();
    }

    private Long resolveMemberId(AccountReceivable accountReceivable) {
        if (accountReceivable.getMember() == null) {
            return null;
        }
        return memberJpaRepository.findEntityByUuid(accountReceivable.getMember().uuid()).orElseThrow().getId();
    }

    private Long resolveStallId(AccountReceivable accountReceivable) {
        if (accountReceivable.getStall() == null) {
            return null;
        }
        return stallJpaRepository.findEntityByUuid(accountReceivable.getStall().uuid()).orElseThrow().getId();
    }

    private Long resolveStatusId(AccountReceivable accountReceivable) {
        return resolveStatusId(accountReceivable.getStatus());
    }

    public Long resolveStatusId(AccountReceivableStatus status) {
        return accountReceivableStatusJpaRepository.findByUuid(status.getUuid()).orElseThrow().getId();
    }
}
