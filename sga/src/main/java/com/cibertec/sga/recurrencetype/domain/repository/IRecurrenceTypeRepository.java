package com.cibertec.sga.recurrencetype.domain.repository;

import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link RecurrenceType}, implementado en {@code infrastructure}.
 */
public interface IRecurrenceTypeRepository {

    List<RecurrenceType> findAll();

    Optional<RecurrenceType> findByUuid(UUID uuid);
}
