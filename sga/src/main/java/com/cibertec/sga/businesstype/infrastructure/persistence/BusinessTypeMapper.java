package com.cibertec.sga.businesstype.infrastructure.persistence;

import com.cibertec.sga.businesstype.domain.model.BusinessType;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link BusinessTypeEntity} (fila de tabla) y {@link BusinessType} (modelo de dominio).
 */
@Component
public class BusinessTypeMapper {

    public BusinessType toDomain(BusinessTypeEntity entity) {
        return BusinessType.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }

    public BusinessTypeEntity toNewEntity(BusinessType businessType) {
        return BusinessTypeEntity.builder()
            .name(businessType.getName())
            .build();
    }
}
