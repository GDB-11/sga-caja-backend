package com.cibertec.sga.expense.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.common.web.ErrorResponse;
import com.cibertec.sga.expense.application.IExpenseService;
import com.cibertec.sga.expense.domain.error.ExpenseError;
import com.cibertec.sga.expense.domain.model.Expense;
import com.cibertec.sga.expense.web.dto.ExpenseResponse;
import com.cibertec.sga.expense.web.dto.RegisterExpenseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoints de egresos (RF-27, RF-28, RF-30), reservados a {@code CashierOperator} según la
 * matriz RBAC del plan.
 */
@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Egresos", description = "Registro individual/masivo, consulta, anulación y procesamiento de egresos")
public class ExpenseController {

    private final IExpenseService expenseService;
    private final ExpenseDtoMapper dtoMapper;

    public ExpenseController(IExpenseService expenseService, ExpenseDtoMapper dtoMapper) {
        this.expenseService = expenseService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Listar egresos (filtro por año/mes, paginado, RF-30)")
    public PagedModel<ExpenseResponse> search(
        @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(expenseService.search(year, month, pageable).map(dtoMapper::toResponse));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Obtener un egreso por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Expense, ExpenseError> result = expenseService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Registrar un egreso individual (RF-27)")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterExpenseRequest request, HttpServletRequest httpRequest) {
        Result<Expense, ExpenseError> result = expenseService.register(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Registrar egresos masivos mediante carga de archivo XLSX (RF-28)")
    public ResponseEntity<?> registerBulk(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest) {
        String fileName = file.getOriginalFilename() == null ? file.getName() : file.getOriginalFilename();
        try {
            Result<List<Expense>, ExpenseError> result = expenseService.registerBulk(fileName, file.getInputStream());
            return ResultResponse.created(result.map(this::toResponseList), httpRequest);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(
                    HttpStatus.BAD_REQUEST.value(), "EXPENSE_FILE_READ_ERROR",
                    "No se pudo leer el archivo: " + e.getMessage(), httpRequest.getRequestURI()
                )
            );
        }
    }

    @PatchMapping("/{uuid}/void")
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Anular un egreso pendiente (RF-30)")
    public ResponseEntity<?> voidExpense(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Expense, ExpenseError> result = expenseService.voidExpense(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PatchMapping("/{uuid}/process")
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Procesar un egreso pendiente y emitir su comprobante (RF-30)")
    public ResponseEntity<?> processExpense(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Expense, ExpenseError> result = expenseService.processExpense(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    private List<ExpenseResponse> toResponseList(List<Expense> expenses) {
        return expenses.stream().map(dtoMapper::toResponse).toList();
    }
}
