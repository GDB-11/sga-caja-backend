package com.cibertec.sga.currency.web.dto;

import java.util.UUID;

public record CurrencyResponse(UUID uuid, String code, String name) {
}
