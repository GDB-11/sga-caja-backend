package com.cibertec.sga.incomecategory.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.incomecategory.domain.error.IncomeCategoryError;
import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.incomecategory.domain.repository.IIncomeCategoryRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IncomeCategoryService implements IIncomeCategoryService {

    private final IIncomeCategoryRepository incomeCategoryRepository;

    public IncomeCategoryService(IIncomeCategoryRepository incomeCategoryRepository) {
        this.incomeCategoryRepository = incomeCategoryRepository;
    }

    @Override
    public List<IncomeCategory> findAll() {
        return incomeCategoryRepository.findAll();
    }

    @Override
    public Result<IncomeCategory, IncomeCategoryError> findByUuid(UUID uuid) {
        return incomeCategoryRepository.findByUuid(uuid)
            .map(Result::<IncomeCategory, IncomeCategoryError>success)
            .orElseGet(() -> Result.failure(new IncomeCategoryError.NotFound(uuid.toString())));
    }
}
