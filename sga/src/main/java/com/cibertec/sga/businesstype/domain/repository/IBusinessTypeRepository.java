package com.cibertec.sga.businesstype.domain.repository;

import com.cibertec.sga.businesstype.domain.model.BusinessType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link BusinessType}, implementado en {@code infrastructure}.
 */
public interface IBusinessTypeRepository {

    List<BusinessType> findAll();

    Optional<BusinessType> findByUuid(UUID uuid);

    boolean existsByName(String name);

    boolean existsByNameAndUuidNot(String name, UUID uuid);

    BusinessType insert(BusinessType businessType);

    BusinessType update(UUID uuid, BusinessType businessType);

    void deleteByUuid(UUID uuid);
}
