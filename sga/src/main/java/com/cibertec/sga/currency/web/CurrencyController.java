package com.cibertec.sga.currency.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.currency.application.ICurrencyService;
import com.cibertec.sga.currency.domain.error.CurrencyError;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.web.dto.CurrencyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de solo lectura para monedas (catálogo sembrado por migración).
 */
@RestController
@RequestMapping("/api/currencies")
@Tag(name = "Monedas", description = "Consulta de monedas")
public class CurrencyController {

    private final ICurrencyService currencyService;
    private final CurrencyDtoMapper dtoMapper;

    public CurrencyController(ICurrencyService currencyService, CurrencyDtoMapper dtoMapper) {
        this.currencyService = currencyService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar monedas")
    public List<CurrencyResponse> findAll() {
        return currencyService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener una moneda por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Currency, CurrencyError> result = currencyService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
