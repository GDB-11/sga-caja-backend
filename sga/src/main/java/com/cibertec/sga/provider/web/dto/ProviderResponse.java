package com.cibertec.sga.provider.web.dto;

import java.util.UUID;

public record ProviderResponse(UUID uuid, String name, String document, boolean active) {
}
