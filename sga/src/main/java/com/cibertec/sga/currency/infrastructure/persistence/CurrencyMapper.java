package com.cibertec.sga.currency.infrastructure.persistence;

import com.cibertec.sga.currency.domain.model.Currency;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link CurrencyEntity} (fila de tabla) y {@link Currency} (modelo de dominio).
 */
@Component
public class CurrencyMapper {

    public Currency toDomain(CurrencyEntity entity) {
        return Currency.builder()
            .uuid(entity.getUuid())
            .code(entity.getCode())
            .name(entity.getName())
            .build();
    }
}
