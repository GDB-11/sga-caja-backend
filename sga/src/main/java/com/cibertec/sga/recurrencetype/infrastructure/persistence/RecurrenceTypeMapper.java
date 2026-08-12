package com.cibertec.sga.recurrencetype.infrastructure.persistence;

import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link RecurrenceTypeEntity} (fila de tabla) y {@link RecurrenceType} (modelo
 * de dominio).
 */
@Component
public class RecurrenceTypeMapper {

    public RecurrenceType toDomain(RecurrenceTypeEntity entity) {
        return RecurrenceType.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }
}
