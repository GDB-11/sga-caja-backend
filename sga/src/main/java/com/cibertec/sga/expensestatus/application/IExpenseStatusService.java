package com.cibertec.sga.expensestatus.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.expensestatus.domain.error.ExpenseStatusError;
import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code ExpenseStatus}: listar y obtener estados de egreso (catálogo de solo
 * lectura). Es la única interfaz que se inyecta en {@code ExpenseStatusController}.
 */
public interface IExpenseStatusService {

    List<ExpenseStatus> findAll();

    Result<ExpenseStatus, ExpenseStatusError> findByUuid(UUID uuid);
}
