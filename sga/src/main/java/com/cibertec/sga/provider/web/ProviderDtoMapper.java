package com.cibertec.sga.provider.web;

import com.cibertec.sga.provider.application.ProviderCommand;
import com.cibertec.sga.provider.domain.model.Provider;
import com.cibertec.sga.provider.web.dto.ProviderRequest;
import com.cibertec.sga.provider.web.dto.ProviderResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Provider} y los DTOs de {@code web}.
 */
@Component
public class ProviderDtoMapper {

    public ProviderResponse toResponse(Provider provider) {
        return new ProviderResponse(provider.getUuid(), provider.getName(), provider.getDocument(), provider.isActive());
    }

    public ProviderCommand toCommand(ProviderRequest request) {
        return new ProviderCommand(request.name(), request.document());
    }
}
