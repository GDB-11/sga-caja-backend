package com.cibertec.sga.receipttype.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.receipttype.domain.error.ReceiptTypeError;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.domain.repository.IReceiptTypeRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ReceiptTypeService implements IReceiptTypeService {

    private final IReceiptTypeRepository receiptTypeRepository;

    public ReceiptTypeService(IReceiptTypeRepository receiptTypeRepository) {
        this.receiptTypeRepository = receiptTypeRepository;
    }

    @Override
    public List<ReceiptType> findAll() {
        return receiptTypeRepository.findAll();
    }

    @Override
    public Result<ReceiptType, ReceiptTypeError> findByUuid(UUID uuid) {
        return receiptTypeRepository.findByUuid(uuid)
            .map(Result::<ReceiptType, ReceiptTypeError>success)
            .orElseGet(() -> Result.failure(new ReceiptTypeError.NotFound(uuid.toString())));
    }
}
