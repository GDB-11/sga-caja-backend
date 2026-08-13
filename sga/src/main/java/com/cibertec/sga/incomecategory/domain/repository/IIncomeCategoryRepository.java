package com.cibertec.sga.incomecategory.domain.repository;

import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link IncomeCategory}, implementado en {@code infrastructure}.
 */
public interface IIncomeCategoryRepository {

    List<IncomeCategory> findAll();

    Optional<IncomeCategory> findByUuid(UUID uuid);
}
