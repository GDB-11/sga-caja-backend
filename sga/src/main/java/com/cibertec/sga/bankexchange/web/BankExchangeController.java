package com.cibertec.sga.bankexchange.web;

import com.cibertec.sga.bankexchange.application.IBankExchangeService;
import com.cibertec.sga.bankexchange.domain.error.BankExchangeError;
import com.cibertec.sga.bankexchange.domain.model.BankExchange;
import com.cibertec.sga.bankexchange.web.dto.BankExchangeResponse;
import com.cibertec.sga.bankexchange.web.dto.CreateBankExchangeRequest;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de canje bancario (RF-24), reservados a {@code CashierOperator} según la matriz
 * RBAC del plan.
 */
@RestController
@RequestMapping("/api/bank-exchanges")
@Tag(name = "Canjes bancarios", description = "Canje de cuentas por cobrar de socios por operaciones bancarias")
public class BankExchangeController {

    private final IBankExchangeService bankExchangeService;
    private final BankExchangeDtoMapper dtoMapper;

    public BankExchangeController(IBankExchangeService bankExchangeService, BankExchangeDtoMapper dtoMapper) {
        this.bankExchangeService = bankExchangeService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Listar canjes bancarios (filtro por banco y por fecha de depósito, paginado, RF-31)")
    public PagedModel<BankExchangeResponse> search(
        @RequestParam(required = false) UUID bankUuid, @RequestParam(required = false) LocalDate date,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(bankExchangeService.search(bankUuid, date, pageable).map(dtoMapper::toResponse));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Obtener un canje bancario por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<BankExchange, BankExchangeError> result = bankExchangeService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Canjear una cuenta por cobrar de un socio por una operación bancaria (RF-24)")
    public ResponseEntity<?> create(@Valid @RequestBody CreateBankExchangeRequest request, HttpServletRequest httpRequest) {
        Result<BankExchange, BankExchangeError> result = bankExchangeService.create(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }
}
