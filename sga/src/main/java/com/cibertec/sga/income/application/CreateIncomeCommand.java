package com.cibertec.sga.income.application;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateIncomeCommand(String depositorName, UUID incomeCategoryUuid, String concept, BigDecimal amount) {
}
