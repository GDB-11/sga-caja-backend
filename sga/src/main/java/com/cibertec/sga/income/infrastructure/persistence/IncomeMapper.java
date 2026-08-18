package com.cibertec.sga.income.infrastructure.persistence;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.infrastructure.persistence.CurrencyJpaRepository;
import com.cibertec.sga.income.domain.model.Income;
import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.incomecategory.infrastructure.persistence.IncomeCategoryJpaRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.infrastructure.persistence.ReceiptJpaRepository;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link IncomeEntity}/{@link IncomeRow} (persistencia) y {@link Income} (modelo
 * de dominio). Resuelve {@code ReceiptId}/{@code IncomeCategoryId}/{@code CurrencyId} a partir
 * de los {@code Uuid} del modelo de dominio vía los {@code JpaRepository} de esos módulos
 * (dependencia infra-a-infra) — el modelo de dominio nunca conoce Ids internos.
 */
@Component
public class IncomeMapper {

    private final ReceiptJpaRepository receiptJpaRepository;
    private final IncomeCategoryJpaRepository incomeCategoryJpaRepository;
    private final CurrencyJpaRepository currencyJpaRepository;

    public IncomeMapper(
        ReceiptJpaRepository receiptJpaRepository,
        IncomeCategoryJpaRepository incomeCategoryJpaRepository,
        CurrencyJpaRepository currencyJpaRepository
    ) {
        this.receiptJpaRepository = receiptJpaRepository;
        this.incomeCategoryJpaRepository = incomeCategoryJpaRepository;
        this.currencyJpaRepository = currencyJpaRepository;
    }

    public IncomeEntity toNewEntity(
        Receipt receipt, String depositorName, IncomeCategory incomeCategory, Currency currency, String concept, BigDecimal amount
    ) {
        Long receiptId = receiptJpaRepository.findEntityByUuid(receipt.getUuid()).orElseThrow().getId();
        Long incomeCategoryId = incomeCategoryJpaRepository.findByUuid(incomeCategory.getUuid()).orElseThrow().getId();
        Long currencyId = currencyJpaRepository.findByUuid(currency.getUuid()).orElseThrow().getId();
        return IncomeEntity.builder()
            .receiptId(receiptId)
            .depositorName(depositorName)
            .incomeCategoryId(incomeCategoryId)
            .currencyId(currencyId)
            .concept(concept)
            .amount(amount)
            .build();
    }

    public Income toDomain(IncomeRow row) {
        ReceiptType receiptType = ReceiptType.builder().uuid(row.getReceiptTypeUuid()).name(row.getReceiptTypeName()).build();
        Receipt receipt = Receipt.builder()
            .uuid(row.getReceiptUuid())
            .receiptType(receiptType)
            .correlativeNumber(row.getReceiptCorrelativeNumber())
            .issueDate(row.getReceiptIssueDate())
            .amount(row.getAmount())
            .build();
        IncomeCategory incomeCategory = IncomeCategory.builder()
            .uuid(row.getIncomeCategoryUuid())
            .name(row.getIncomeCategoryName())
            .build();
        Currency currency = Currency.builder()
            .uuid(row.getCurrencyUuid())
            .code(row.getCurrencyCode())
            .name(row.getCurrencyName())
            .build();

        return Income.builder()
            .uuid(row.getUuid())
            .receipt(receipt)
            .depositorName(row.getDepositorName())
            .incomeCategory(incomeCategory)
            .currency(currency)
            .concept(row.getConcept())
            .amount(row.getAmount())
            .build();
    }
}
