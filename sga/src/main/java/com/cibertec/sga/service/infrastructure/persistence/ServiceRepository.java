package com.cibertec.sga.service.infrastructure.persistence;

import com.cibertec.sga.service.domain.model.Service;
import com.cibertec.sga.service.domain.repository.IServiceRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceRepository implements IServiceRepository {

    private final ServiceJpaRepository jpaRepository;
    private final ServiceMapper mapper;

    public ServiceRepository(ServiceJpaRepository jpaRepository, ServiceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Service> search(String search, Boolean active, Pageable pageable) {
        return jpaRepository.search(search, active, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<Service> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Service insert(Service service) {
        ServiceEntity saved = jpaRepository.save(mapper.toNewEntity(service));
        return findByUuid(saved.getUuid()).orElseThrow();
    }

    @Override
    public Service update(UUID uuid, Service service) {
        ServiceEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        mapper.updateEntity(entity, service);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }

    @Override
    public Service deactivate(UUID uuid) {
        ServiceEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setActive(false);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }
}
