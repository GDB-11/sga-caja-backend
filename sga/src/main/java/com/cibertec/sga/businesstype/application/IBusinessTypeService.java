package com.cibertec.sga.businesstype.application;

import com.cibertec.sga.businesstype.domain.error.BusinessTypeError;
import com.cibertec.sga.businesstype.domain.model.BusinessType;
import com.cibertec.sga.common.result.Result;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code BusinessType} (RF-08): listar, obtener, crear, editar y eliminar
 * giros comerciales. Es la única interfaz que se inyecta en {@code BusinessTypeController}.
 */
public interface IBusinessTypeService {

    List<BusinessType> findAll();

    Result<BusinessType, BusinessTypeError> findByUuid(UUID uuid);

    Result<BusinessType, BusinessTypeError> create(String name);

    Result<BusinessType, BusinessTypeError> update(UUID uuid, String name);

    Result<Void, BusinessTypeError> delete(UUID uuid);
}
