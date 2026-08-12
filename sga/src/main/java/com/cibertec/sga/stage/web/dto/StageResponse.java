package com.cibertec.sga.stage.web.dto;

import java.util.UUID;

public record StageResponse(UUID uuid, short code, String name) {
}
