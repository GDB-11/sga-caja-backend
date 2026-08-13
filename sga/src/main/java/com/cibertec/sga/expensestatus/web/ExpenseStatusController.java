package com.cibertec.sga.expensestatus.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.expensestatus.application.IExpenseStatusService;
import com.cibertec.sga.expensestatus.domain.error.ExpenseStatusError;
import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.expensestatus.web.dto.ExpenseStatusResponse;
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
 * Endpoints de solo lectura para estados de egreso (catálogo sembrado por migración).
 */
@RestController
@RequestMapping("/api/expense-statuses")
@Tag(name = "Estados de egreso", description = "Consulta de estados de comprobantes de egreso")
public class ExpenseStatusController {

    private final IExpenseStatusService expenseStatusService;
    private final ExpenseStatusDtoMapper dtoMapper;

    public ExpenseStatusController(IExpenseStatusService expenseStatusService, ExpenseStatusDtoMapper dtoMapper) {
        this.expenseStatusService = expenseStatusService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar estados de egreso")
    public List<ExpenseStatusResponse> findAll() {
        return expenseStatusService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un estado de egreso por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<ExpenseStatus, ExpenseStatusError> result = expenseStatusService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
