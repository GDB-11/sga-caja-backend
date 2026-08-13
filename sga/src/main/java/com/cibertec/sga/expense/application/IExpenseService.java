package com.cibertec.sga.expense.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.expense.domain.error.ExpenseError;
import com.cibertec.sga.expense.domain.model.Expense;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code Expense} (RF-27, RF-28, RF-30): registrar egresos individuales o
 * masivos, listar, obtener, anular y procesar. Es la única interfaz que se inyecta en
 * {@code ExpenseController}.
 */
public interface IExpenseService {

    Result<Expense, ExpenseError> register(RegisterExpenseCommand command);

    Result<List<Expense>, ExpenseError> registerBulk(String fileName, InputStream fileContent);

    Result<Expense, ExpenseError> findByUuid(UUID uuid);

    Page<Expense> search(Integer year, Integer month, Pageable pageable);

    Result<Expense, ExpenseError> voidExpense(UUID uuid);

    Result<Expense, ExpenseError> processExpense(UUID uuid);
}
