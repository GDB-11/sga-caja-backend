package com.cibertec.sga.stall.web;

import com.cibertec.sga.stall.application.StallCommand;
import com.cibertec.sga.stall.domain.model.Stall;
import com.cibertec.sga.stall.web.dto.StallRequest;
import com.cibertec.sga.stall.web.dto.StallResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Stall} y los DTOs de {@code web}.
 */
@Component
public class StallDtoMapper {

    public StallResponse toResponse(Stall stall) {
        StallResponse.MemberRef memberRef = stall.getMember() == null
            ? null
            : new StallResponse.MemberRef(stall.getMember().uuid(), stall.getMember().fullName());
        return new StallResponse(
            stall.getUuid(),
            stall.getNumber(),
            new StallResponse.BusinessTypeRef(stall.getBusinessType().getUuid(), stall.getBusinessType().getName()),
            memberRef,
            stall.getTenantName(),
            stall.getTenantDocument(),
            stall.getValidityStartDate(),
            stall.getValidityEndDate(),
            stall.isActive()
        );
    }

    public StallCommand toCommand(StallRequest request) {
        return new StallCommand(
            request.number(), request.businessTypeUuid(), request.memberUuid(), request.tenantName(),
            request.tenantDocument(), request.validityStartDate(), request.validityEndDate()
        );
    }
}
