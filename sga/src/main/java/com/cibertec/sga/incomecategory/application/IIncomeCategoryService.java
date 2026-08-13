package com.cibertec.sga.incomecategory.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.incomecategory.domain.error.IncomeCategoryError;
import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code IncomeCategory}: listar y obtener categorías de ingreso (catálogo de
 * solo lectura). Es la única interfaz que se inyecta en {@code IncomeCategoryController}.
 */
public interface IIncomeCategoryService {

    List<IncomeCategory> findAll();

    Result<IncomeCategory, IncomeCategoryError> findByUuid(UUID uuid);
}
