package com.cibertec.sga.stall.application;

import com.cibertec.sga.businesstype.domain.model.BusinessType;
import com.cibertec.sga.businesstype.domain.repository.IBusinessTypeRepository;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.member.domain.model.Member;
import com.cibertec.sga.member.domain.repository.IMemberRepository;
import com.cibertec.sga.stall.domain.error.StallError;
import com.cibertec.sga.stall.domain.model.MemberSummary;
import com.cibertec.sga.stall.domain.model.Stall;
import com.cibertec.sga.stall.domain.repository.IStallRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StallService implements IStallService {

    private final IStallRepository stallRepository;
    private final IBusinessTypeRepository businessTypeRepository;
    private final IMemberRepository memberRepository;

    public StallService(
        IStallRepository stallRepository, IBusinessTypeRepository businessTypeRepository, IMemberRepository memberRepository
    ) {
        this.stallRepository = stallRepository;
        this.businessTypeRepository = businessTypeRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Page<Stall> search(String search, Boolean active, Pageable pageable) {
        return stallRepository.search(search, active, pageable);
    }

    @Override
    public Result<Stall, StallError> findByUuid(UUID uuid) {
        return stallRepository.findByUuid(uuid)
            .map(Result::<Stall, StallError>success)
            .orElseGet(() -> Result.failure(new StallError.NotFound(uuid.toString())));
    }

    @Override
    public Result<Stall, StallError> create(StallCommand command) {
        if (stallRepository.existsByNumber(command.number())) {
            return Result.failure(new StallError.DuplicateNumber(command.number()));
        }

        Result<Stall, StallError> validation = validateAndBuild(null, command);
        if (validation.isFailure()) {
            return validation;
        }

        return Result.success(stallRepository.insert(validation.getValue()));
    }

    @Override
    public Result<Stall, StallError> update(UUID uuid, StallCommand command) {
        if (stallRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new StallError.NotFound(uuid.toString()));
        }

        if (stallRepository.existsByNumberAndUuidNot(command.number(), uuid)) {
            return Result.failure(new StallError.DuplicateNumber(command.number()));
        }

        Result<Stall, StallError> validation = validateAndBuild(uuid, command);
        if (validation.isFailure()) {
            return validation;
        }

        return Result.success(stallRepository.update(uuid, validation.getValue()));
    }

    @Override
    public Result<Stall, StallError> deactivate(UUID uuid) {
        if (stallRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new StallError.NotFound(uuid.toString()));
        }

        return Result.success(stallRepository.deactivate(uuid));
    }

    @Override
    public Result<Stall, StallError> activate(UUID uuid) {
        if (stallRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new StallError.NotFound(uuid.toString()));
        }

        return Result.success(stallRepository.activate(uuid));
    }

    private Result<Stall, StallError> validateAndBuild(UUID uuid, StallCommand command) {
        if (command.validityStartDate() != null && command.validityEndDate() != null
            && command.validityEndDate().isBefore(command.validityStartDate())) {
            return Result.failure(new StallError.InvalidValidityPeriod());
        }

        Optional<BusinessType> businessType = businessTypeRepository.findByUuid(command.businessTypeUuid());
        if (businessType.isEmpty()) {
            return Result.failure(new StallError.BusinessTypeNotFound(command.businessTypeUuid().toString()));
        }

        MemberSummary memberSummary = null;
        if (command.memberUuid() != null) {
            Optional<Member> member = memberRepository.findByUuid(command.memberUuid());
            if (member.isEmpty()) {
                return Result.failure(new StallError.MemberNotFound(command.memberUuid().toString()));
            }
            memberSummary = new MemberSummary(
                member.get().getUuid(), member.get().getFirstName() + " " + member.get().getLastName()
            );
        }

        Stall stall = Stall.builder()
            .uuid(uuid)
            .number(command.number())
            .businessType(businessType.get())
            .member(memberSummary)
            .tenantName(command.tenantName())
            .tenantDocument(command.tenantDocument())
            .validityStartDate(command.validityStartDate())
            .validityEndDate(command.validityEndDate())
            .build();
        return Result.success(stall);
    }
}
