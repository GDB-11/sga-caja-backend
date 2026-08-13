package com.cibertec.sga.expensestatus.domain.repository;

import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link ExpenseStatus}, implementado en {@code infrastructure}.
 */
public interface IExpenseStatusRepository {

    List<ExpenseStatus> findAll();

    Optional<ExpenseStatus> findByUuid(UUID uuid);

    Optional<ExpenseStatus> findByName(String name);
}
