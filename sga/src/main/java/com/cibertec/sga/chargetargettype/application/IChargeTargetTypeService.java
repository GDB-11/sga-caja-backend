package com.cibertec.sga.chargetargettype.application;

import com.cibertec.sga.chargetargettype.domain.error.ChargeTargetTypeError;
import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.common.result.Result;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code ChargeTargetType}: listar y obtener destinos de cobro (catálogo de
 * solo lectura). Es la única interfaz que se inyecta en {@code ChargeTargetTypeController}.
 */
public interface IChargeTargetTypeService {

    List<ChargeTargetType> findAll();

    Result<ChargeTargetType, ChargeTargetTypeError> findByUuid(UUID uuid);
}
