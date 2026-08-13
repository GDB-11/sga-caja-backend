package com.cibertec.sga.receipttype.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.receipttype.application.IReceiptTypeService;
import com.cibertec.sga.receipttype.domain.error.ReceiptTypeError;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.web.dto.ReceiptTypeResponse;
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
 * Endpoints de solo lectura para tipos de comprobante (catálogo sembrado por migración).
 */
@RestController
@RequestMapping("/api/receipt-types")
@Tag(name = "Tipos de comprobante", description = "Consulta de tipos de comprobante correlativo")
public class ReceiptTypeController {

    private final IReceiptTypeService receiptTypeService;
    private final ReceiptTypeDtoMapper dtoMapper;

    public ReceiptTypeController(IReceiptTypeService receiptTypeService, ReceiptTypeDtoMapper dtoMapper) {
        this.receiptTypeService = receiptTypeService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar tipos de comprobante")
    public List<ReceiptTypeResponse> findAll() {
        return receiptTypeService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un tipo de comprobante por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<ReceiptType, ReceiptTypeError> result = receiptTypeService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
