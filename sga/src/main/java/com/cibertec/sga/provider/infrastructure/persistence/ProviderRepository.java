package com.cibertec.sga.provider.infrastructure.persistence;

import com.cibertec.sga.provider.domain.model.Provider;
import com.cibertec.sga.provider.domain.repository.IProviderRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderRepository implements IProviderRepository {

    private final ProviderJpaRepository jpaRepository;
    private final ProviderMapper mapper;

    public ProviderRepository(ProviderJpaRepository jpaRepository, ProviderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Provider> search(String search, Boolean active, Pageable pageable) {
        return jpaRepository.search(search, active, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<Provider> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<Provider> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public Provider insert(Provider provider) {
        return mapper.toDomain(jpaRepository.save(mapper.toNewEntity(provider)));
    }

    @Override
    public Provider update(UUID uuid, Provider provider) {
        ProviderEntity entity = jpaRepository.findByUuid(uuid).orElseThrow();
        entity.setName(provider.getName());
        entity.setDocument(provider.getDocument());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Provider deactivate(UUID uuid) {
        ProviderEntity entity = jpaRepository.findByUuid(uuid).orElseThrow();
        entity.setActive(false);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Provider activate(UUID uuid) {
        ProviderEntity entity = jpaRepository.findByUuid(uuid).orElseThrow();
        entity.setActive(true);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
