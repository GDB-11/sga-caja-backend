package com.cibertec.sga.recurrencetype.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.recurrencetype.domain.error.RecurrenceTypeError;
import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import com.cibertec.sga.recurrencetype.domain.repository.IRecurrenceTypeRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RecurrenceTypeService implements IRecurrenceTypeService {

    private final IRecurrenceTypeRepository recurrenceTypeRepository;

    public RecurrenceTypeService(IRecurrenceTypeRepository recurrenceTypeRepository) {
        this.recurrenceTypeRepository = recurrenceTypeRepository;
    }

    @Override
    public List<RecurrenceType> findAll() {
        return recurrenceTypeRepository.findAll();
    }

    @Override
    public Result<RecurrenceType, RecurrenceTypeError> findByUuid(UUID uuid) {
        return recurrenceTypeRepository.findByUuid(uuid)
            .map(Result::<RecurrenceType, RecurrenceTypeError>success)
            .orElseGet(() -> Result.failure(new RecurrenceTypeError.NotFound(uuid.toString())));
    }
}
