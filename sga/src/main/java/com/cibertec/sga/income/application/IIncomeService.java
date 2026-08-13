package com.cibertec.sga.income.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.income.domain.error.IncomeError;
import com.cibertec.sga.income.domain.model.Income;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code Income} (RF-25, RF-29): registrar un ingreso externo, listar (filtro
 * por fecha) y obtener. Es la única interfaz que se inyecta en {@code IncomeController}.
 */
public interface IIncomeService {

    Result<Income, IncomeError> create(CreateIncomeCommand command);

    Result<Income, IncomeError> findByUuid(UUID uuid);

    Page<Income> search(UUID incomeCategoryUuid, LocalDate date, Pageable pageable);
}
