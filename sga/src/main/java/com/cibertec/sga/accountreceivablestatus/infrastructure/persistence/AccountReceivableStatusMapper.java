package com.cibertec.sga.accountreceivablestatus.infrastructure.persistence;

import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link AccountReceivableStatusEntity} (fila de tabla) y
 * {@link AccountReceivableStatus} (modelo de dominio).
 */
@Component
public class AccountReceivableStatusMapper {

    public AccountReceivableStatus toDomain(AccountReceivableStatusEntity entity) {
        return AccountReceivableStatus.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }
}
