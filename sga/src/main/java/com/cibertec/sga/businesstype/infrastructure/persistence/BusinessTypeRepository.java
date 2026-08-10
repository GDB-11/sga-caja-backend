package com.cibertec.sga.businesstype.infrastructure.persistence;

import com.cibertec.sga.businesstype.domain.model.BusinessType;
import com.cibertec.sga.businesstype.domain.repository.IBusinessTypeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class BusinessTypeRepository implements IBusinessTypeRepository {

    private final BusinessTypeJpaRepository jpaRepository;
    private final BusinessTypeMapper mapper;

    public BusinessTypeRepository(BusinessTypeJpaRepository jpaRepository, BusinessTypeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<BusinessType> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<BusinessType> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndUuidNot(String name, UUID uuid) {
        return jpaRepository.existsByNameAndUuidNot(name, uuid);
    }

    @Override
    public BusinessType insert(BusinessType businessType) {
        BusinessTypeEntity entity = mapper.toNewEntity(businessType);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public BusinessType update(UUID uuid, BusinessType businessType) {
        BusinessTypeEntity entity = jpaRepository.findByUuid(uuid).orElseThrow();
        entity.setName(businessType.getName());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        jpaRepository.findByUuid(uuid).ifPresent(jpaRepository::delete);
    }
}
