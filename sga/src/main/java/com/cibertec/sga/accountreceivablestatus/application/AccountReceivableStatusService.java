package com.cibertec.sga.accountreceivablestatus.application;

import com.cibertec.sga.accountreceivablestatus.domain.error.AccountReceivableStatusError;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.accountreceivablestatus.domain.repository.IAccountReceivableStatusRepository;
import com.cibertec.sga.common.result.Result;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccountReceivableStatusService implements IAccountReceivableStatusService {

    private final IAccountReceivableStatusRepository accountReceivableStatusRepository;

    public AccountReceivableStatusService(IAccountReceivableStatusRepository accountReceivableStatusRepository) {
        this.accountReceivableStatusRepository = accountReceivableStatusRepository;
    }

    @Override
    public List<AccountReceivableStatus> findAll() {
        return accountReceivableStatusRepository.findAll();
    }

    @Override
    public Result<AccountReceivableStatus, AccountReceivableStatusError> findByUuid(UUID uuid) {
        return accountReceivableStatusRepository.findByUuid(uuid)
            .map(Result::<AccountReceivableStatus, AccountReceivableStatusError>success)
            .orElseGet(() -> Result.failure(new AccountReceivableStatusError.NotFound(uuid.toString())));
    }
}
