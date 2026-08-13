package com.cibertec.sga.accountreceivable.domain.repository;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivableMovement;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link AccountReceivable}, implementado en {@code infrastructure}.
 */
public interface IAccountReceivableRepository {

    Page<AccountReceivable> search(UUID serviceUuid, UUID memberUuid, UUID stallUuid, Pageable pageable);

    Optional<AccountReceivable> findByUuid(UUID uuid);

    List<AccountReceivable> insertAll(List<AccountReceivable> accountReceivables);

    /**
     * Actualiza el monto de una cuenta por cobrar ya generada — usado por el módulo
     * {@code consumptionreading} tras calcular el importe por consumo (RF-17), nunca por edición
     * directa del usuario.
     */
    AccountReceivable updateAmount(UUID uuid, BigDecimal amount);

    /**
     * Actualiza el estado de una cuenta por cobrar — usado por {@code markExempt} (RF-21) y por
     * los módulos {@code payment}/{@code bankexchange} para marcarla como pagada tras liquidarla
     * (RF-23/RF-24).
     */
    AccountReceivable updateStatus(UUID uuid, AccountReceivableStatus status);

    List<AccountReceivableMovement> findMovementsByMember(UUID memberUuid);

    List<AccountReceivableMovement> findMovementsByStall(UUID stallUuid);
}
