package com.cibertec.sga.businesstype.application;

import com.cibertec.sga.businesstype.domain.error.BusinessTypeError;
import com.cibertec.sga.businesstype.domain.model.BusinessType;
import com.cibertec.sga.businesstype.domain.repository.IBusinessTypeRepository;
import com.cibertec.sga.common.result.Result;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BusinessTypeService implements IBusinessTypeService {

    private final IBusinessTypeRepository businessTypeRepository;

    public BusinessTypeService(IBusinessTypeRepository businessTypeRepository) {
        this.businessTypeRepository = businessTypeRepository;
    }

    @Override
    public List<BusinessType> findAll() {
        return businessTypeRepository.findAll();
    }

    @Override
    public Result<BusinessType, BusinessTypeError> findByUuid(UUID uuid) {
        return businessTypeRepository.findByUuid(uuid)
            .map(Result::<BusinessType, BusinessTypeError>success)
            .orElseGet(() -> Result.failure(new BusinessTypeError.NotFound(uuid.toString())));
    }

    @Override
    public Result<BusinessType, BusinessTypeError> create(String name) {
        if (businessTypeRepository.existsByName(name)) {
            return Result.failure(new BusinessTypeError.DuplicateName(name));
        }

        BusinessType businessType = BusinessType.builder().name(name).build();
        return Result.success(businessTypeRepository.insert(businessType));
    }

    @Override
    public Result<BusinessType, BusinessTypeError> update(UUID uuid, String name) {
        if (businessTypeRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new BusinessTypeError.NotFound(uuid.toString()));
        }

        if (businessTypeRepository.existsByNameAndUuidNot(name, uuid)) {
            return Result.failure(new BusinessTypeError.DuplicateName(name));
        }

        BusinessType businessType = BusinessType.builder().uuid(uuid).name(name).build();
        return Result.success(businessTypeRepository.update(uuid, businessType));
    }

    @Override
    public Result<Void, BusinessTypeError> delete(UUID uuid) {
        if (businessTypeRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new BusinessTypeError.NotFound(uuid.toString()));
        }

        businessTypeRepository.deleteByUuid(uuid);
        return Result.success(null);
    }
}
