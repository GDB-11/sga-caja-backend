package com.cibertec.sga.bankexchange.domain.repository;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.bankexchange.domain.model.BankExchange;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link BankExchange}, implementado en {@code infrastructure}.
 */
public interface IBankExchangeRepository {

    /**
     * Crea el {@code BankExchange} para un {@link Receipt} ya emitido (RF-24). El
     * {@code Receipt} se crea antes, vía {@code IReceiptRepository}, en la misma transacción de
     * {@code BankExchangeService}.
     */
    BankExchange create(AccountReceivable accountReceivable, Bank bank, Receipt receipt, LocalDate depositDate);

    Optional<BankExchange> findByUuid(UUID uuid);

    /**
     * {@code date} filtra por fecha de depósito (RF-31, "listar recibos bancarios por fecha").
     */
    Page<BankExchange> search(UUID bankUuid, LocalDate date, Pageable pageable);
}
