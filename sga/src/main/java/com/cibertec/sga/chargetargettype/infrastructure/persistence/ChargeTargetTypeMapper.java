package com.cibertec.sga.chargetargettype.infrastructure.persistence;

import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ChargeTargetTypeEntity} (fila de tabla) y {@link ChargeTargetType}
 * (modelo de dominio).
 */
@Component
public class ChargeTargetTypeMapper {

    public ChargeTargetType toDomain(ChargeTargetTypeEntity entity) {
        return ChargeTargetType.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }
}
