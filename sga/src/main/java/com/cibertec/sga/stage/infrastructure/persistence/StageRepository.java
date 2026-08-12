package com.cibertec.sga.stage.infrastructure.persistence;

import com.cibertec.sga.stage.domain.model.Stage;
import com.cibertec.sga.stage.domain.repository.IStageRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class StageRepository implements IStageRepository {

    private final StageJpaRepository jpaRepository;
    private final StageMapper mapper;

    public StageRepository(StageJpaRepository jpaRepository, StageMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Stage> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Stage> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }
}
