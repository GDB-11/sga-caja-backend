package com.cibertec.sga.stall.infrastructure.persistence;

import com.cibertec.sga.stall.domain.model.Stall;
import com.cibertec.sga.stall.domain.repository.IStallRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class StallRepository implements IStallRepository {

    private final StallJpaRepository jpaRepository;
    private final StallMapper mapper;

    public StallRepository(StallJpaRepository jpaRepository, StallMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Stall> search(String search, Boolean active, Pageable pageable) {
        return jpaRepository.search(search, active, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<Stall> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNumber(String number) {
        return jpaRepository.existsByNumber(number);
    }

    @Override
    public boolean existsByNumberAndUuidNot(String number, UUID uuid) {
        return jpaRepository.existsByNumberAndUuidNot(number, uuid);
    }

    @Override
    public Stall insert(Stall stall) {
        StallEntity saved = jpaRepository.save(mapper.toNewEntity(stall));
        return findByUuid(saved.getUuid()).orElseThrow();
    }

    @Override
    public Stall update(UUID uuid, Stall stall) {
        StallEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        mapper.updateEntity(entity, stall);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }

    @Override
    public Stall deactivate(UUID uuid) {
        StallEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setActive(false);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }
}
