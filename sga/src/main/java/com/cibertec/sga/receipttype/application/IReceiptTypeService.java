package com.cibertec.sga.receipttype.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.receipttype.domain.error.ReceiptTypeError;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code ReceiptType}: listar y obtener tipos de comprobante (catálogo de solo
 * lectura). Es la única interfaz que se inyecta en {@code ReceiptTypeController}.
 */
public interface IReceiptTypeService {

    List<ReceiptType> findAll();

    Result<ReceiptType, ReceiptTypeError> findByUuid(UUID uuid);
}
