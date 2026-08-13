package com.cibertec.sga.accountreceivable.application;

import com.cibertec.sga.accountreceivable.domain.error.AccountReceivableError;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivableMovement;
import com.cibertec.sga.common.result.Result;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code AccountReceivable} (RF-16, RF-18, RF-21, RF-26): listar, obtener,
 * generar cuentas por cobrar para puestos o para socios, exonerar una cuenta y consultar el
 * resumen de cuentas/movimientos de un socio o puesto. Es la única interfaz que se inyecta en
 * {@code AccountReceivableController}.
 */
public interface IAccountReceivableService {

    Page<AccountReceivable> search(UUID serviceUuid, UUID memberUuid, UUID stallUuid, Pageable pageable);

    Result<AccountReceivable, AccountReceivableError> findByUuid(UUID uuid);

    Result<List<AccountReceivable>, AccountReceivableError> generateByStall(GenerateByStallCommand command);

    Result<List<AccountReceivable>, AccountReceivableError> generateByMember(GenerateByMemberCommand command);

    /**
     * Marca una cuenta por cobrar pendiente como exonerada (RF-21, RN-03). Falla con
     * {@link AccountReceivableError.NotPending} si la cuenta ya fue pagada o exonerada.
     */
    Result<AccountReceivable, AccountReceivableError> markExempt(UUID uuid);

    /**
     * Resumen de cuentas por cobrar y movimientos relacionados (pago o canje) de un socio o
     * puesto, para abrir en otra ventana (RF-26). Exactamente uno de los dos Uuid debe venir
     * informado.
     */
    Result<List<AccountReceivableMovement>, AccountReceivableError> summary(UUID memberUuid, UUID stallUuid);
}
