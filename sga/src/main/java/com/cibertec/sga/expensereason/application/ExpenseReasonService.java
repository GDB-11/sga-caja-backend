package com.cibertec.sga.expensereason.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.expensereason.domain.error.ExpenseReasonError;
import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import com.cibertec.sga.expensereason.domain.repository.IExpenseReasonRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExpenseReasonService implements IExpenseReasonService {

    private final IExpenseReasonRepository expenseReasonRepository;

    public ExpenseReasonService(IExpenseReasonRepository expenseReasonRepository) {
        this.expenseReasonRepository = expenseReasonRepository;
    }

    @Override
    public List<ExpenseReason> findAll() {
        return expenseReasonRepository.findAll();
    }

    @Override
    public Result<ExpenseReason, ExpenseReasonError> findByUuid(UUID uuid) {
        return expenseReasonRepository.findByUuid(uuid)
            .map(Result::<ExpenseReason, ExpenseReasonError>success)
            .orElseGet(() -> Result.failure(new ExpenseReasonError.NotFound(uuid.toString())));
    }
}
