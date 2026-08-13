package com.cibertec.sga.receipttype.web;

import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.web.dto.ReceiptTypeResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link ReceiptType} y los DTOs de {@code web}.
 */
@Component
public class ReceiptTypeDtoMapper {

    public ReceiptTypeResponse toResponse(ReceiptType receiptType) {
        return new ReceiptTypeResponse(receiptType.getUuid(), receiptType.getName());
    }
}
