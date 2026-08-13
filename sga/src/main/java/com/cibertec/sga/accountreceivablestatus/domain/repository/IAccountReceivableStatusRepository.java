package com.cibertec.sga.accountreceivablestatus.domain.repository;

import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link AccountReceivableStatus}, implementado en
 * {@code infrastructure}.
 */
public interface IAccountReceivableStatusRepository {

    List<AccountReceivableStatus> findAll();

    Optional<AccountReceivableStatus> findByUuid(UUID uuid);

    Optional<AccountReceivableStatus> findByName(String name);
}
