package com.cibertec.sga.bank.domain.repository;

import com.cibertec.sga.bank.domain.model.Bank;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link Bank}, implementado en {@code infrastructure}.
 */
public interface IBankRepository {

    Page<Bank> search(String search, Boolean active, Pageable pageable);

    Optional<Bank> findByUuid(UUID uuid);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByAccountNumberAndUuidNot(String accountNumber, UUID uuid);

    Bank insert(Bank bank);

    Bank update(UUID uuid, Bank bank);

    Bank deactivate(UUID uuid);
}
