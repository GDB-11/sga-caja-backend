package com.cibertec.sga.recurrencetype.web;

import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import com.cibertec.sga.recurrencetype.web.dto.RecurrenceTypeResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link RecurrenceType} y los DTOs de {@code web}.
 */
@Component
public class RecurrenceTypeDtoMapper {

    public RecurrenceTypeResponse toResponse(RecurrenceType recurrenceType) {
        return new RecurrenceTypeResponse(recurrenceType.getUuid(), recurrenceType.getName());
    }
}
