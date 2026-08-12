package com.cibertec.sga.currency.infrastructure.persistence;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.domain.repository.ICurrencyRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CurrencyRepository implements ICurrencyRepository {

    private final CurrencyJpaRepository jpaRepository;
    private final CurrencyMapper mapper;

    public CurrencyRepository(CurrencyJpaRepository jpaRepository, CurrencyMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Currency> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Currency> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }
}
