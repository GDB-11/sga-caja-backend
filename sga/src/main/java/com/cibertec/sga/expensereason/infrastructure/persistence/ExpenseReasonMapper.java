package com.cibertec.sga.expensereason.infrastructure.persistence;

import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ExpenseReasonEntity} (fila de tabla) y {@link ExpenseReason} (modelo de
 * dominio).
 */
@Component
public class ExpenseReasonMapper {

    public ExpenseReason toDomain(ExpenseReasonEntity entity) {
        return ExpenseReason.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }
}
