package com.cibertec.sga.income.domain.repository;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.income.domain.model.Income;
import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link Income}, implementado en {@code infrastructure}.
 */
public interface IIncomeRepository {

    /**
     * Crea el {@code Income} para un {@link Receipt} ya emitido (RF-25). El {@code Receipt} se
     * crea antes, vía {@code IReceiptRepository}, en la misma transacción de
     * {@code IncomeService}.
     */
    Income create(
        Receipt receipt, String depositorName, IncomeCategory incomeCategory, Currency currency, String concept, BigDecimal amount
    );

    Optional<Income> findByUuid(UUID uuid);

    /**
     * {@code date} filtra por la fecha de emisión del comprobante (RF-29, "listar recibos de
     * ingreso por fecha").
     */
    Page<Income> search(UUID incomeCategoryUuid, LocalDate date, Pageable pageable);
}
