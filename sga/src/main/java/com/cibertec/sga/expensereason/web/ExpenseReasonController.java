package com.cibertec.sga.expensereason.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.expensereason.application.IExpenseReasonService;
import com.cibertec.sga.expensereason.domain.error.ExpenseReasonError;
import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import com.cibertec.sga.expensereason.web.dto.ExpenseReasonResponse;
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
 * Endpoints de solo lectura para motivos de egreso (catálogo sembrado por migración).
 */
@RestController
@RequestMapping("/api/expense-reasons")
@Tag(name = "Motivos de egreso", description = "Consulta de motivos de egreso")
public class ExpenseReasonController {

    private final IExpenseReasonService expenseReasonService;
    private final ExpenseReasonDtoMapper dtoMapper;

    public ExpenseReasonController(IExpenseReasonService expenseReasonService, ExpenseReasonDtoMapper dtoMapper) {
        this.expenseReasonService = expenseReasonService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar motivos de egreso")
    public List<ExpenseReasonResponse> findAll() {
        return expenseReasonService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un motivo de egreso por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<ExpenseReason, ExpenseReasonError> result = expenseReasonService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
