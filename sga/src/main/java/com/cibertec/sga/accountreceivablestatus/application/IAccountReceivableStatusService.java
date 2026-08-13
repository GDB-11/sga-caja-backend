package com.cibertec.sga.accountreceivablestatus.application;

import com.cibertec.sga.accountreceivablestatus.domain.error.AccountReceivableStatusError;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.common.result.Result;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code AccountReceivableStatus}: listar y obtener estados de cuenta por
 * cobrar (catálogo de solo lectura). Es la única interfaz que se inyecta en
 * {@code AccountReceivableStatusController}.
 */
public interface IAccountReceivableStatusService {

    List<AccountReceivableStatus> findAll();

    Result<AccountReceivableStatus, AccountReceivableStatusError> findByUuid(UUID uuid);
}
