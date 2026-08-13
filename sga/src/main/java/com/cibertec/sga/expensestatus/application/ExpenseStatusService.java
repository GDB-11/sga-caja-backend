package com.cibertec.sga.expensestatus.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.expensestatus.domain.error.ExpenseStatusError;
import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.expensestatus.domain.repository.IExpenseStatusRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExpenseStatusService implements IExpenseStatusService {

    private final IExpenseStatusRepository expenseStatusRepository;

    public ExpenseStatusService(IExpenseStatusRepository expenseStatusRepository) {
        this.expenseStatusRepository = expenseStatusRepository;
    }

    @Override
    public List<ExpenseStatus> findAll() {
        return expenseStatusRepository.findAll();
    }

    @Override
    public Result<ExpenseStatus, ExpenseStatusError> findByUuid(UUID uuid) {
        return expenseStatusRepository.findByUuid(uuid)
            .map(Result::<ExpenseStatus, ExpenseStatusError>success)
            .orElseGet(() -> Result.failure(new ExpenseStatusError.NotFound(uuid.toString())));
    }
}
