package com.cibertec.sga.provider.infrastructure.persistence;

import com.cibertec.sga.provider.domain.model.Provider;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ProviderEntity} (fila de tabla) y {@link Provider} (modelo de dominio).
 */
@Component
public class ProviderMapper {

    public Provider toDomain(ProviderEntity entity) {
        return Provider.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .document(entity.getDocument())
            .active(entity.isActive())
            .build();
    }

    public ProviderEntity toNewEntity(Provider provider) {
        return ProviderEntity.builder()
            .name(provider.getName())
            .document(provider.getDocument())
            .build();
    }
}
