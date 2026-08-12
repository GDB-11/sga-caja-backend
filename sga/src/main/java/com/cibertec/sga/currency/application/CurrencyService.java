package com.cibertec.sga.currency.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.currency.domain.error.CurrencyError;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.domain.repository.ICurrencyRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService implements ICurrencyService {

    private final ICurrencyRepository currencyRepository;

    public CurrencyService(ICurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    @Override
    public Result<Currency, CurrencyError> findByUuid(UUID uuid) {
        return currencyRepository.findByUuid(uuid)
            .map(Result::<Currency, CurrencyError>success)
            .orElseGet(() -> Result.failure(new CurrencyError.NotFound(uuid.toString())));
    }
}
