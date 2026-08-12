package com.cibertec.sga.role.infrastructure.persistence;

import com.cibertec.sga.role.domain.model.Role;
import com.cibertec.sga.role.domain.repository.IRoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository implements IRoleRepository {

    private final RoleJpaRepository jpaRepository;
    private final RoleMapper mapper;

    public RoleRepository(RoleJpaRepository jpaRepository, RoleMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Role> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Role> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }
}
