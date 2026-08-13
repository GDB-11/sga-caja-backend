package com.cibertec.sga.incomecategory.infrastructure.persistence;

import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link IncomeCategoryEntity} (fila de tabla) y {@link IncomeCategory} (modelo
 * de dominio).
 */
@Component
public class IncomeCategoryMapper {

    public IncomeCategory toDomain(IncomeCategoryEntity entity) {
        return IncomeCategory.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }
}
