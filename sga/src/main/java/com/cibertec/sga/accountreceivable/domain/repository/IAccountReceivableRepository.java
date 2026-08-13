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

    /**
     * Igual que {@link #findByUuid(UUID)}, pero toma un bloqueo pesimista de fila
     * ({@code SELECT ... FOR UPDATE}) sobre la cuenta por cobrar dentro de la transacción del
     * llamador — evita la carrera de "doble liquidación" cuando dos operaciones concurrentes
     * (pago, canje, exoneración) leen la misma cuenta en estado {@code Pending} antes de que
     * cualquiera de las dos confirme su cambio de estado (RNF-04). Solo debe usarse dentro de un
     * método {@code @Transactional} inmediatamente antes de validar y mutar el estado.
     */
    Optional<AccountReceivable> findByUuidForUpdate(UUID uuid);

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

    /**
     * Cuentas por cobrar cargadas directamente a un socio ("reporte de socios", RF-33) cuyo
     * período de cobro (RF-16) inicia en el mes indicado.
     */
    List<AccountReceivableMovement> findMovementsByMemberPeriod(int year, int month);

    /**
     * Cuentas por cobrar cargadas directamente a un puesto ("reporte de no socios", RF-33) cuyo
     * período de cobro (RF-16) inicia en el mes indicado.
     */
    List<AccountReceivableMovement> findMovementsByStallPeriod(int year, int month);
}
