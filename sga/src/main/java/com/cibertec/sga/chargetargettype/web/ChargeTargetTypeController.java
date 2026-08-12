package com.cibertec.sga.chargetargettype.web;

import com.cibertec.sga.chargetargettype.application.IChargeTargetTypeService;
import com.cibertec.sga.chargetargettype.domain.error.ChargeTargetTypeError;
import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.chargetargettype.web.dto.ChargeTargetTypeResponse;
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
 * Endpoints de solo lectura para destinos de cobro (catálogo sembrado por migración).
 */
@RestController
@RequestMapping("/api/charge-target-types")
@Tag(name = "Destinos de cobro", description = "Consulta de destinos de cobro de servicios (Cargo a)")
public class ChargeTargetTypeController {

    private final IChargeTargetTypeService chargeTargetTypeService;
    private final ChargeTargetTypeDtoMapper dtoMapper;

    public ChargeTargetTypeController(
        IChargeTargetTypeService chargeTargetTypeService, ChargeTargetTypeDtoMapper dtoMapper
    ) {
        this.chargeTargetTypeService = chargeTargetTypeService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar destinos de cobro")
    public List<ChargeTargetTypeResponse> findAll() {
        return chargeTargetTypeService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un destino de cobro por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<ChargeTargetType, ChargeTargetTypeError> result = chargeTargetTypeService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
