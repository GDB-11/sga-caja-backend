package com.cibertec.sga.accountreceivablestatus.web;

import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.accountreceivablestatus.web.dto.AccountReceivableStatusResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link AccountReceivableStatus} y los DTOs de {@code web}.
 */
@Component
public class AccountReceivableStatusDtoMapper {

    public AccountReceivableStatusResponse toResponse(AccountReceivableStatus status) {
        return new AccountReceivableStatusResponse(status.getUuid(), status.getName());
    }
}
