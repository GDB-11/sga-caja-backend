package com.cibertec.sga.bank.infrastructure.persistence;

import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.infrastructure.persistence.CurrencyJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link BankEntity}/{@link BankRow} (persistencia) y {@link Bank} (modelo de
 * dominio). Resuelve el {@code CurrencyId} interno a partir del {@code Uuid} de la moneda del
 * modelo de dominio vía {@link CurrencyJpaRepository} (dependencia infra-a-infra) — el modelo
 * de dominio nunca conoce Ids internos.
 */
@Component
public class BankMapper {

    private final CurrencyJpaRepository currencyJpaRepository;

    public BankMapper(CurrencyJpaRepository currencyJpaRepository) {
        this.currencyJpaRepository = currencyJpaRepository;
    }

    public Bank toDomain(BankRow row) {
        Currency currency = Currency.builder()
            .uuid(row.getCurrencyUuid())
            .code(row.getCurrencyCode())
            .name(row.getCurrencyName())
            .build();
        return Bank.builder()
            .uuid(row.getUuid())
            .name(row.getName())
            .accountNumber(row.getAccountNumber())
            .cci(row.getCci())
            .currency(currency)
            .active(row.getIsActive())
            .build();
    }

    public BankEntity toNewEntity(Bank bank) {
        return BankEntity.builder()
            .name(bank.getName())
            .accountNumber(bank.getAccountNumber())
            .cci(bank.getCci())
            .currencyId(resolveCurrencyId(bank))
            .build();
    }

    public void updateEntity(BankEntity entity, Bank bank) {
        entity.setName(bank.getName());
        entity.setAccountNumber(bank.getAccountNumber());
        entity.setCci(bank.getCci());
        entity.setCurrencyId(resolveCurrencyId(bank));
    }

    private Long resolveCurrencyId(Bank bank) {
        return currencyJpaRepository.findByUuid(bank.getCurrency().getUuid()).orElseThrow().getId();
    }
}
