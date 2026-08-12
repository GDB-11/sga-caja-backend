package com.cibertec.sga.chargetargettype.web;

import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.chargetargettype.web.dto.ChargeTargetTypeResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link ChargeTargetType} y los DTOs de {@code web}.
 */
@Component
public class ChargeTargetTypeDtoMapper {

    public ChargeTargetTypeResponse toResponse(ChargeTargetType chargeTargetType) {
        return new ChargeTargetTypeResponse(chargeTargetType.getUuid(), chargeTargetType.getName());
    }
}
