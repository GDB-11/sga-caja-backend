package com.cibertec.sga.chargetargettype.application;

import com.cibertec.sga.chargetargettype.domain.error.ChargeTargetTypeError;
import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.chargetargettype.domain.repository.IChargeTargetTypeRepository;
import com.cibertec.sga.common.result.Result;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ChargeTargetTypeService implements IChargeTargetTypeService {

    private final IChargeTargetTypeRepository chargeTargetTypeRepository;

    public ChargeTargetTypeService(IChargeTargetTypeRepository chargeTargetTypeRepository) {
        this.chargeTargetTypeRepository = chargeTargetTypeRepository;
    }

    @Override
    public List<ChargeTargetType> findAll() {
        return chargeTargetTypeRepository.findAll();
    }

    @Override
    public Result<ChargeTargetType, ChargeTargetTypeError> findByUuid(UUID uuid) {
        return chargeTargetTypeRepository.findByUuid(uuid)
            .map(Result::<ChargeTargetType, ChargeTargetTypeError>success)
            .orElseGet(() -> Result.failure(new ChargeTargetTypeError.NotFound(uuid.toString())));
    }
}
