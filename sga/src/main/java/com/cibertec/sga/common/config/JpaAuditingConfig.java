package com.cibertec.sga.common.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita JPA auditing ({@code @CreatedDate}/{@code @LastModifiedDate} y, cuando aplique,
 * {@code @CreatedBy}/{@code @LastModifiedBy}) sobre las {@code Entity}.
 *
 * <p>El {@link AuditorAware} real (leyendo el {@code Id} interno del usuario autenticado)
 * se conecta en la Fase 1 junto con el filtro JWT; hasta entonces no hay entidades con
 * columnas {@code CreatedBy}/{@code UpdatedBy} obligatorias que dependan de este bean.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return Optional::empty;
    }
}
