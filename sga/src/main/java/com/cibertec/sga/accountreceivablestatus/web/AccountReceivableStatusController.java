package com.cibertec.sga.accountreceivablestatus.web;

import com.cibertec.sga.accountreceivablestatus.application.IAccountReceivableStatusService;
import com.cibertec.sga.accountreceivablestatus.domain.error.AccountReceivableStatusError;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.accountreceivablestatus.web.dto.AccountReceivableStatusResponse;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
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
 * Endpoints de solo lectura para estados de cuenta por cobrar (catálogo sembrado por
 * migración: Pending, Paid, Exempt — RN-03).
 */
@RestController
@RequestMapping("/api/account-receivable-statuses")
@Tag(name = "Estados de cuenta por cobrar", description = "Consulta de estados de cuenta por cobrar")
public class AccountReceivableStatusController {

    private final IAccountReceivableStatusService accountReceivableStatusService;
    private final AccountReceivableStatusDtoMapper dtoMapper;

    public AccountReceivableStatusController(
        IAccountReceivableStatusService accountReceivableStatusService, AccountReceivableStatusDtoMapper dtoMapper
    ) {
        this.accountReceivableStatusService = accountReceivableStatusService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar estados de cuenta por cobrar")
    public List<AccountReceivableStatusResponse> findAll() {
        return accountReceivableStatusService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un estado de cuenta por cobrar por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<AccountReceivableStatus, AccountReceivableStatusError> result = accountReceivableStatusService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
