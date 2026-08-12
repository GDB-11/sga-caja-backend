package com.cibertec.sga.currency.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.currency.domain.error.CurrencyError;
import com.cibertec.sga.currency.domain.model.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code Currency}: listar y obtener monedas (catálogo de solo lectura).
 * Es la única interfaz que se inyecta en {@code CurrencyController}.
 */
public interface ICurrencyService {

    List<Currency> findAll();

    Result<Currency, CurrencyError> findByUuid(UUID uuid);
}
