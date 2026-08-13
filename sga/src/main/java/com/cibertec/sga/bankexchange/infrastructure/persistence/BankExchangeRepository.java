package com.cibertec.sga.bankexchange.infrastructure.persistence;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.bankexchange.domain.model.BankExchange;
import com.cibertec.sga.bankexchange.domain.repository.IBankExchangeRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class BankExchangeRepository implements IBankExchangeRepository {

    private final BankExchangeJpaRepository jpaRepository;
    private final BankExchangeMapper mapper;

    public BankExchangeRepository(BankExchangeJpaRepository jpaRepository, BankExchangeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public BankExchange create(AccountReceivable accountReceivable, Bank bank, Receipt receipt, LocalDate depositDate) {
        BankExchangeEntity saved = jpaRepository.save(mapper.toNewEntity(accountReceivable, bank, receipt, depositDate));
        return findByUuid(saved.getUuid()).orElseThrow();
    }

    @Override
    public Optional<BankExchange> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Page<BankExchange> search(UUID bankUuid, LocalDate date, Pageable pageable) {
        return jpaRepository.search(bankUuid, date, pageable).map(mapper::toDomain);
    }

    @Override
    public List<BankExchange> findByDepositDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findRowsByDepositDateBetween(startDate, endDate).stream().map(mapper::toDomain).toList();
    }
}
