package com.cibertec.sga.user.infrastructure.persistence;

import com.cibertec.sga.role.infrastructure.persistence.RoleEntity;
import com.cibertec.sga.role.infrastructure.persistence.RoleJpaRepository;
import com.cibertec.sga.user.domain.model.User;
import com.cibertec.sga.user.domain.repository.IUserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de {@link IUserRepository}. Resuelve el {@code Role} asociado vía
 * {@link RoleJpaRepository} (acceso directo entre repositorios JPA de infraestructura, no a
 * través del puerto {@code IRoleRepository}) para desnormalizar el nombre del rol en
 * {@link User} sin exponer el {@code Id} interno fuera de esta capa.
 */
@Repository
public class UserRepository implements IUserRepository {

    private final UserJpaRepository jpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserMapper mapper;

    public UserRepository(UserJpaRepository jpaRepository, RoleJpaRepository roleJpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomainWithRole);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(this::toDomainWithRole);
    }

    private User toDomainWithRole(UserEntity entity) {
        RoleEntity role = roleJpaRepository.findById(entity.getRoleId()).orElseThrow();
        return mapper.toDomain(entity, role);
    }
}
