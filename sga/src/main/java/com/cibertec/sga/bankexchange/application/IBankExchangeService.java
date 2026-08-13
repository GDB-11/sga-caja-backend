package com.cibertec.sga.bankexchange.application;

import com.cibertec.sga.bankexchange.domain.error.BankExchangeError;
import com.cibertec.sga.bankexchange.domain.model.BankExchange;
import com.cibertec.sga.common.result.Result;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code BankExchange} (RF-24, RF-31): canjear una cuenta por cobrar de un
 * socio por una operación bancaria, listar (filtro por fecha) y obtener. Es la única interfaz
 * que se inyecta en {@code BankExchangeController}.
 */
public interface IBankExchangeService {

    Result<BankExchange, BankExchangeError> create(CreateBankExchangeCommand command);

    Result<BankExchange, BankExchangeError> findByUuid(UUID uuid);

    Page<BankExchange> search(UUID bankUuid, LocalDate date, Pageable pageable);
}
