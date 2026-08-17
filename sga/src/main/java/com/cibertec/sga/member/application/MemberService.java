package com.cibertec.sga.member.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.member.domain.error.MemberError;
import com.cibertec.sga.member.domain.model.Member;
import com.cibertec.sga.member.domain.repository.IMemberRepository;
import com.cibertec.sga.stage.domain.model.Stage;
import com.cibertec.sga.stage.domain.repository.IStageRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MemberService implements IMemberService {

    private final IMemberRepository memberRepository;
    private final IStageRepository stageRepository;

    public MemberService(IMemberRepository memberRepository, IStageRepository stageRepository) {
        this.memberRepository = memberRepository;
        this.stageRepository = stageRepository;
    }

    @Override
    public Page<Member> search(String search, Boolean active, Pageable pageable) {
        return memberRepository.search(search, active, pageable);
    }

    @Override
    public Result<Member, MemberError> findByUuid(UUID uuid) {
        return memberRepository.findByUuid(uuid)
            .map(Result::<Member, MemberError>success)
            .orElseGet(() -> Result.failure(new MemberError.NotFound(uuid.toString())));
    }

    @Override
    public Result<Member, MemberError> create(MemberCommand command) {
        if (memberRepository.existsByCode(command.code())) {
            return Result.failure(new MemberError.DuplicateCode(command.code()));
        }

        Optional<Stage> stage = stageRepository.findByUuid(command.stageUuid());
        if (stage.isEmpty()) {
            return Result.failure(new MemberError.StageNotFound(command.stageUuid().toString()));
        }

        Member member = Member.builder()
            .code(command.code())
            .firstName(command.firstName())
            .lastName(command.lastName())
            .shareNumber(command.shareNumber())
            .stage(stage.get())
            .birthDate(command.birthDate())
            .build();
        return Result.success(memberRepository.insert(member));
    }

    @Override
    public Result<Member, MemberError> update(UUID uuid, MemberCommand command) {
        if (memberRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new MemberError.NotFound(uuid.toString()));
        }

        if (memberRepository.existsByCodeAndUuidNot(command.code(), uuid)) {
            return Result.failure(new MemberError.DuplicateCode(command.code()));
        }

        Optional<Stage> stage = stageRepository.findByUuid(command.stageUuid());
        if (stage.isEmpty()) {
            return Result.failure(new MemberError.StageNotFound(command.stageUuid().toString()));
        }

        Member member = Member.builder()
            .uuid(uuid)
            .code(command.code())
            .firstName(command.firstName())
            .lastName(command.lastName())
            .shareNumber(command.shareNumber())
            .stage(stage.get())
            .birthDate(command.birthDate())
            .build();
        return Result.success(memberRepository.update(uuid, member));
    }

    @Override
    public Result<Member, MemberError> deactivate(UUID uuid) {
        if (memberRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new MemberError.NotFound(uuid.toString()));
        }

        return Result.success(memberRepository.deactivate(uuid));
    }

    @Override
    public Result<Member, MemberError> activate(UUID uuid) {
        if (memberRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new MemberError.NotFound(uuid.toString()));
        }

        return Result.success(memberRepository.activate(uuid));
    }
}
