package com.cibertec.sga.expensereason.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.expensereason.domain.error.ExpenseReasonError;
import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code ExpenseReason}: listar y obtener motivos de egreso (catálogo de solo
 * lectura). Es la única interfaz que se inyecta en {@code ExpenseReasonController}.
 */
public interface IExpenseReasonService {

    List<ExpenseReason> findAll();

    Result<ExpenseReason, ExpenseReasonError> findByUuid(UUID uuid);
}
