package com.cibertec.sga.member.web;

import com.cibertec.sga.member.application.MemberCommand;
import com.cibertec.sga.member.domain.model.Member;
import com.cibertec.sga.member.web.dto.MemberRequest;
import com.cibertec.sga.member.web.dto.MemberResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Member} y los DTOs de {@code web}.
 */
@Component
public class MemberDtoMapper {

    public MemberResponse toResponse(Member member) {
        return new MemberResponse(
            member.getUuid(),
            member.getCode(),
            member.getFirstName(),
            member.getLastName(),
            member.getShareNumber(),
            new MemberResponse.StageRef(member.getStage().getUuid(), member.getStage().getName()),
            member.getBirthDate(),
            member.isActive()
        );
    }

    public MemberCommand toCommand(MemberRequest request) {
        return new MemberCommand(
            request.code(), request.firstName(), request.lastName(), request.shareNumber(),
            request.stageUuid(), request.birthDate()
        );
    }
}
