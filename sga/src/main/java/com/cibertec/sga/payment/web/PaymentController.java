package com.cibertec.sga.payment.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.payment.application.IPaymentService;
import com.cibertec.sga.payment.application.PaymentTotal;
import com.cibertec.sga.payment.domain.error.PaymentError;
import com.cibertec.sga.payment.domain.model.Payment;
import com.cibertec.sga.payment.web.dto.ProcessPaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de pago de cuentas por cobrar (RF-22, RF-23), reservados a {@code CashierOperator}
 * según la matriz RBAC del plan ("Collections/payments/income/bank exchange").
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Pagos", description = "Cálculo de totales y procesamiento de pagos de cuentas por cobrar")
public class PaymentController {

    private final IPaymentService paymentService;
    private final PaymentDtoMapper dtoMapper;

    public PaymentController(IPaymentService paymentService, PaymentDtoMapper dtoMapper) {
        this.paymentService = paymentService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/compute-total")
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Calcular el total de las cuentas por cobrar seleccionadas (RF-22)")
    public ResponseEntity<?> computeTotal(@Valid @RequestBody ProcessPaymentRequest request, HttpServletRequest httpRequest) {
        Result<PaymentTotal, PaymentError> result = paymentService.computeTotal(request.accountReceivableUuids());
        return ResultResponse.ok(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PostMapping
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Procesar el pago de las cuentas por cobrar seleccionadas y emitir el recibo (RF-23)")
    public ResponseEntity<?> processPayment(@Valid @RequestBody ProcessPaymentRequest request, HttpServletRequest httpRequest) {
        Result<Payment, PaymentError> result = paymentService.processPayment(request.accountReceivableUuids());
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Obtener un pago por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Payment, PaymentError> result = paymentService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
