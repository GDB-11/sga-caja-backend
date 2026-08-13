package com.cibertec.sga.expensestatus.infrastructure.persistence;

import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ExpenseStatusEntity} (fila de tabla) y {@link ExpenseStatus} (modelo de
 * dominio).
 */
@Component
public class ExpenseStatusMapper {

    public ExpenseStatus toDomain(ExpenseStatusEntity entity) {
        return ExpenseStatus.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }
}
