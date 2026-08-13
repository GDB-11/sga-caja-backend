package com.cibertec.sga.consumptionreading.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.consumptionreading.application.IConsumptionReadingService;
import com.cibertec.sga.consumptionreading.domain.error.ConsumptionReadingError;
import com.cibertec.sga.consumptionreading.domain.model.ConsumptionReading;
import com.cibertec.sga.consumptionreading.web.dto.ConsumptionReadingResponse;
import com.cibertec.sga.consumptionreading.web.dto.RegisterConsumptionReadingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de lecturas de consumo (RF-17, RN-05): registrar y consultar. Disponible tanto para
 * Administrator como para CashierOperator, según la matriz RBAC del plan (RF-16–18).
 */
@RestController
@RequestMapping("/api/consumption-readings")
@Tag(name = "Lecturas de consumo", description = "Registro y consulta de lecturas de servicios por consumo")
public class ConsumptionReadingController {

    private final IConsumptionReadingService consumptionReadingService;
    private final ConsumptionReadingDtoMapper dtoMapper;

    public ConsumptionReadingController(IConsumptionReadingService consumptionReadingService, ConsumptionReadingDtoMapper dtoMapper) {
        this.consumptionReadingService = consumptionReadingService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    @Operation(summary = "Registrar la lectura inicial/final de una cuenta por cobrar de consumo (RF-17)")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterConsumptionReadingRequest request, HttpServletRequest httpRequest) {
        Result<ConsumptionReading, ConsumptionReadingError> result = consumptionReadingService.register(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener una lectura de consumo por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<ConsumptionReading, ConsumptionReadingError> result = consumptionReadingService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @GetMapping("/by-account-receivable/{accountReceivableUuid}")
    @Operation(summary = "Obtener la lectura de consumo de una cuenta por cobrar")
    public ResponseEntity<?> findByAccountReceivableUuid(@PathVariable UUID accountReceivableUuid, HttpServletRequest request) {
        Result<ConsumptionReading, ConsumptionReadingError> result =
            consumptionReadingService.findByAccountReceivableUuid(accountReceivableUuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
