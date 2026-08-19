package com.cibertec.sga.receipt.infrastructure.persistence;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.infrastructure.persistence.CurrencyJpaRepository;
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
    private final CurrencyJpaRepository currencyJpaRepository;

    public ReceiptMapper(ReceiptTypeJpaRepository receiptTypeJpaRepository, CurrencyJpaRepository currencyJpaRepository) {
        this.receiptTypeJpaRepository = receiptTypeJpaRepository;
        this.currencyJpaRepository = currencyJpaRepository;
    }

    public ReceiptEntity toNewEntity(Receipt receipt) {
        Long receiptTypeId = receiptTypeJpaRepository.findByUuid(receipt.getReceiptType().getUuid()).orElseThrow().getId();
        Long currencyId = currencyJpaRepository.findByUuid(receipt.getCurrency().getUuid()).orElseThrow().getId();
        return ReceiptEntity.builder()
            .receiptTypeId(receiptTypeId)
            .amount(receipt.getAmount())
            .description(receipt.getDescription())
            .currencyId(currencyId)
            .build();
    }

    public Receipt toDomain(ReceiptEntity entity, ReceiptType receiptType, Currency currency) {
        return Receipt.builder()
            .uuid(entity.getUuid())
            .receiptType(receiptType)
            .correlativeNumber(entity.getCorrelativeNumber())
            .issueDate(entity.getIssueDate())
            .amount(entity.getAmount())
            .description(entity.getDescription())
            .currency(currency)
            .build();
    }

    public Receipt toDomain(ReceiptRow row) {
        Currency currency = Currency.builder()
            .uuid(row.getCurrencyUuid())
            .code(row.getCurrencyCode())
            .name(row.getCurrencyName())
            .build();
        return Receipt.builder()
            .uuid(row.getUuid())
            .receiptType(ReceiptType.builder().uuid(row.getReceiptTypeUuid()).name(row.getReceiptTypeName()).build())
            .correlativeNumber(row.getCorrelativeNumber())
            .issueDate(row.getIssueDate())
            .amount(row.getAmount())
            .description(row.getDescription())
            .currency(currency)
            .build();
    }
}
