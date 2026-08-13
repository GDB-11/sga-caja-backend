package com.cibertec.sga.receipt.domain.repository;

import com.cibertec.sga.receipt.domain.model.Receipt;

/**
 * Puerto de persistencia para {@link Receipt}, implementado en {@code infrastructure}. Sin
 * CRUD manual: los comprobantes solo se crean como parte de una operación de pago, canje o
 * ingreso externo (RF-23–RF-25) — nunca directamente por el usuario.
 */
public interface IReceiptRepository {

    Receipt insert(Receipt receipt);
}
