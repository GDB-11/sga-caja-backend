package com.cibertec.sga.expense.domain.repository;

import com.cibertec.sga.expense.domain.model.Expense;
import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link Expense}, implementado en {@code infrastructure}.
 */
public interface IExpenseRepository {

    Expense insert(Expense expense);

    List<Expense> insertAll(List<Expense> expenses);

    Optional<Expense> findByUuid(UUID uuid);

    /**
     * Lista egresos, opcionalmente filtrados por año/mes de {@code ExpenseDate} (RF-30, "por
     * mes").
     */
    Page<Expense> search(Integer year, Integer month, Pageable pageable);

    /**
     * Actualiza el estado de un egreso — usado para anular (RF-30, "anular").
     */
    Expense updateStatus(UUID uuid, ExpenseStatus status);

    /**
     * Marca un egreso como procesado, asociándole el {@link Receipt} recién emitido (RF-30,
     * "procesar").
     */
    Expense markProcessed(UUID uuid, ExpenseStatus processedStatus, Receipt receipt);
}
