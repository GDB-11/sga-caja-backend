package com.cibertec.sga.bank.application;

import com.cibertec.sga.bank.domain.error.BankError;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.common.result.Result;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code Bank} (RF-12): listar (búsqueda + paginación), obtener, crear, editar
 * y desactivar bancos. Es la única interfaz que se inyecta en {@code BankController}.
 */
public interface IBankService {

    Page<Bank> search(String search, Boolean active, Pageable pageable);

    Result<Bank, BankError> findByUuid(UUID uuid);

    Result<Bank, BankError> create(BankCommand command);

    Result<Bank, BankError> update(UUID uuid, BankCommand command);

    Result<Bank, BankError> deactivate(UUID uuid);

    Result<Bank, BankError> activate(UUID uuid);
}
