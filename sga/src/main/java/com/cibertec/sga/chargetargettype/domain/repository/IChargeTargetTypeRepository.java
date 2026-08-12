package com.cibertec.sga.chargetargettype.domain.repository;

import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link ChargeTargetType}, implementado en {@code infrastructure}.
 */
public interface IChargeTargetTypeRepository {

    List<ChargeTargetType> findAll();

    Optional<ChargeTargetType> findByUuid(UUID uuid);
}
