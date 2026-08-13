package com.cibertec.sga.expensereason.domain.repository;

import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link ExpenseReason}, implementado en {@code infrastructure}.
 */
public interface IExpenseReasonRepository {

    List<ExpenseReason> findAll();

    Optional<ExpenseReason> findByUuid(UUID uuid);

    Optional<ExpenseReason> findByName(String name);
}
