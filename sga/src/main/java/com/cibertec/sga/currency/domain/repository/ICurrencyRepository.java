package com.cibertec.sga.currency.domain.repository;

import com.cibertec.sga.currency.domain.model.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link Currency}, implementado en {@code infrastructure}.
 */
public interface ICurrencyRepository {

    List<Currency> findAll();

    Optional<Currency> findByUuid(UUID uuid);

    Optional<Currency> findByCode(String code);
}
