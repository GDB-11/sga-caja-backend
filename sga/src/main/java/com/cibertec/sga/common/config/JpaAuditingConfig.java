package com.cibertec.sga.common.config;

import com.cibertec.sga.common.security.AuthenticatedUser;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Habilita JPA auditing ({@code @CreatedDate}/{@code @LastModifiedDate} y, cuando aplique,
 * {@code @CreatedBy}/{@code @LastModifiedBy}) sobre las {@code Entity}, leyendo el {@code Id}
 * interno del usuario autenticado desde el {@link AuthenticatedUser} que deja
 * {@code JwtAuthenticationFilter} en el {@code SecurityContext}.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
                return Optional.empty();
            }
            return Optional.of(user.id());
        };
    }
}
