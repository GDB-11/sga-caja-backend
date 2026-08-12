package com.cibertec.sga.chargetargettype.infrastructure.persistence;

import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.chargetargettype.domain.repository.IChargeTargetTypeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ChargeTargetTypeRepository implements IChargeTargetTypeRepository {

    private final ChargeTargetTypeJpaRepository jpaRepository;
    private final ChargeTargetTypeMapper mapper;

    public ChargeTargetTypeRepository(ChargeTargetTypeJpaRepository jpaRepository, ChargeTargetTypeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ChargeTargetType> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ChargeTargetType> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }
}
