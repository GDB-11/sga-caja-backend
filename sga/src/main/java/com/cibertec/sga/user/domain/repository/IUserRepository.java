package com.cibertec.sga.user.domain.repository;

import com.cibertec.sga.user.domain.model.User;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link User}, consumido por el módulo {@code auth}.
 */
public interface IUserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findByUuid(UUID uuid);
}
