package com.cibertec.sga.recurrencetype.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.recurrencetype.domain.error.RecurrenceTypeError;
import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code RecurrenceType}: listar y obtener recurrencias (catálogo de solo
 * lectura). Es la única interfaz que se inyecta en {@code RecurrenceTypeController}.
 */
public interface IRecurrenceTypeService {

    List<RecurrenceType> findAll();

    Result<RecurrenceType, RecurrenceTypeError> findByUuid(UUID uuid);
}
