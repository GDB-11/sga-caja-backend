package com.cibertec.sga.income.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.income.application.IIncomeService;
import com.cibertec.sga.income.domain.error.IncomeError;
import com.cibertec.sga.income.domain.model.Income;
import com.cibertec.sga.income.web.dto.CreateIncomeRequest;
import com.cibertec.sga.income.web.dto.IncomeResponse;
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
 * Endpoints de ingresos externos (RF-25), reservados a {@code CashierOperator} según la matriz
 * RBAC del plan.
 */
@RestController
@RequestMapping("/api/incomes")
@Tag(name = "Ingresos externos", description = "Registro de ingresos externos a caja")
public class IncomeController {

    private final IIncomeService incomeService;
    private final IncomeDtoMapper dtoMapper;

    public IncomeController(IIncomeService incomeService, IncomeDtoMapper dtoMapper) {
        this.incomeService = incomeService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Listar ingresos externos (filtro por categoría y por fecha, paginado, RF-29)")
    public PagedModel<IncomeResponse> search(
        @RequestParam(required = false) UUID incomeCategoryUuid, @RequestParam(required = false) LocalDate date,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(incomeService.search(incomeCategoryUuid, date, pageable).map(dtoMapper::toResponse));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Obtener un ingreso externo por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Income, IncomeError> result = incomeService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Registrar un ingreso externo (RF-25)")
    public ResponseEntity<?> create(@Valid @RequestBody CreateIncomeRequest request, HttpServletRequest httpRequest) {
        Result<Income, IncomeError> result = incomeService.create(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }
}
