package com.cibertec.sga.currency.web;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.web.dto.CurrencyResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Currency} y los DTOs de {@code web}.
 */
@Component
public class CurrencyDtoMapper {

    public CurrencyResponse toResponse(Currency currency) {
        return new CurrencyResponse(currency.getUuid(), currency.getCode(), currency.getName());
    }
}
