package com.cibertec.sga.bankexchange.web;

import com.cibertec.sga.accountreceivable.web.AccountReceivableDtoMapper;
import com.cibertec.sga.bankexchange.application.CreateBankExchangeCommand;
import com.cibertec.sga.bankexchange.domain.model.BankExchange;
import com.cibertec.sga.bankexchange.web.dto.BankExchangeResponse;
import com.cibertec.sga.bankexchange.web.dto.CreateBankExchangeRequest;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link BankExchange} y los DTOs de {@code web}. Reutiliza
 * {@link AccountReceivableDtoMapper} para la cuenta por cobrar anidada, en vez de duplicar su
 * mapeo.
 */
@Component
public class BankExchangeDtoMapper {

    private final AccountReceivableDtoMapper accountReceivableDtoMapper;

    public BankExchangeDtoMapper(AccountReceivableDtoMapper accountReceivableDtoMapper) {
        this.accountReceivableDtoMapper = accountReceivableDtoMapper;
    }

    public BankExchangeResponse toResponse(BankExchange bankExchange) {
        return new BankExchangeResponse(
            bankExchange.getUuid(),
            accountReceivableDtoMapper.toResponse(bankExchange.getAccountReceivable()),
            new BankExchangeResponse.BankRef(bankExchange.getBank().getUuid(), bankExchange.getBank().getName()),
            new BankExchangeResponse.ReceiptRef(
                bankExchange.getReceipt().getUuid(), bankExchange.getReceipt().getReceiptType().getName(),
                bankExchange.getReceipt().getCorrelativeNumber(), bankExchange.getReceipt().getIssueDate()
            ),
            bankExchange.getDepositDate(),
            bankExchange.getAmount(),
            new BankExchangeResponse.CurrencyRef(
                bankExchange.getCurrency().getUuid(), bankExchange.getCurrency().getCode(), bankExchange.getCurrency().getName()
            )
        );
    }

    public CreateBankExchangeCommand toCommand(CreateBankExchangeRequest request) {
        return new CreateBankExchangeCommand(request.accountReceivableUuid(), request.bankUuid(), request.depositDate());
    }
}
