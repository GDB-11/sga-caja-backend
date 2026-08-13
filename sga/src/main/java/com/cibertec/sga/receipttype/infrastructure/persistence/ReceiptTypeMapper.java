package com.cibertec.sga.receipttype.infrastructure.persistence;

import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ReceiptTypeEntity} (fila de tabla) y {@link ReceiptType} (modelo de
 * dominio).
 */
@Component
public class ReceiptTypeMapper {

    public ReceiptType toDomain(ReceiptTypeEntity entity) {
        return ReceiptType.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }
}
