package com.cibertec.sga.receipt.infrastructure.persistence;

import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.infrastructure.persistence.ReceiptTypeJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ReceiptEntity} (fila de tabla) y {@link Receipt} (modelo de dominio).
 * Resuelve {@code ReceiptTypeId} a partir del {@code Uuid} del modelo de dominio vía
 * {@link ReceiptTypeJpaRepository} (dependencia infra-a-infra) — el modelo de dominio nunca
 * conoce Ids internos.
 */
@Component
public class ReceiptMapper {

    private final ReceiptTypeJpaRepository receiptTypeJpaRepository;

    public ReceiptMapper(ReceiptTypeJpaRepository receiptTypeJpaRepository) {
        this.receiptTypeJpaRepository = receiptTypeJpaRepository;
    }

    public ReceiptEntity toNewEntity(Receipt receipt) {
        Long receiptTypeId = receiptTypeJpaRepository.findByUuid(receipt.getReceiptType().getUuid()).orElseThrow().getId();
        return ReceiptEntity.builder()
            .receiptTypeId(receiptTypeId)
            .amount(receipt.getAmount())
            .description(receipt.getDescription())
            .build();
    }

    public Receipt toDomain(ReceiptEntity entity, ReceiptType receiptType) {
        return Receipt.builder()
            .uuid(entity.getUuid())
            .receiptType(receiptType)
            .correlativeNumber(entity.getCorrelativeNumber())
            .issueDate(entity.getIssueDate())
            .amount(entity.getAmount())
            .description(entity.getDescription())
            .build();
    }
}
