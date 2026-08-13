package com.cibertec.sga.bankexchange.application;

import java.time.LocalDate;
import java.util.UUID;

public record CreateBankExchangeCommand(UUID accountReceivableUuid, UUID bankUuid, LocalDate depositDate) {
}
