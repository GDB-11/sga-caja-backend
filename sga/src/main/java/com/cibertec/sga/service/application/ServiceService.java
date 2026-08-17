package com.cibertec.sga.service.application;

import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.chargetargettype.domain.repository.IChargeTargetTypeRepository;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.domain.repository.ICurrencyRepository;
import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import com.cibertec.sga.recurrencetype.domain.repository.IRecurrenceTypeRepository;
import com.cibertec.sga.service.domain.error.ServiceError;
import com.cibertec.sga.service.domain.model.Service;
import com.cibertec.sga.service.domain.repository.IServiceRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@org.springframework.stereotype.Service
public class ServiceService implements IServiceService {

    private final IServiceRepository serviceRepository;
    private final IRecurrenceTypeRepository recurrenceTypeRepository;
    private final IChargeTargetTypeRepository chargeTargetTypeRepository;
    private final ICurrencyRepository currencyRepository;

    public ServiceService(
        IServiceRepository serviceRepository,
        IRecurrenceTypeRepository recurrenceTypeRepository,
        IChargeTargetTypeRepository chargeTargetTypeRepository,
        ICurrencyRepository currencyRepository
    ) {
        this.serviceRepository = serviceRepository;
        this.recurrenceTypeRepository = recurrenceTypeRepository;
        this.chargeTargetTypeRepository = chargeTargetTypeRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public Page<Service> search(String search, Boolean active, Pageable pageable) {
        return serviceRepository.search(search, active, pageable);
    }

    @Override
    public Result<Service, ServiceError> findByUuid(UUID uuid) {
        return serviceRepository.findByUuid(uuid)
            .map(Result::<Service, ServiceError>success)
            .orElseGet(() -> Result.failure(new ServiceError.NotFound(uuid.toString())));
    }

    @Override
    public Result<Service, ServiceError> create(ServiceCommand command) {
        Result<Service, ServiceError> validation = validateAndBuild(null, command);
        if (validation.isFailure()) {
            return validation;
        }
        return Result.success(serviceRepository.insert(validation.getValue()));
    }

    @Override
    public Result<Service, ServiceError> update(UUID uuid, ServiceCommand command) {
        if (serviceRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new ServiceError.NotFound(uuid.toString()));
        }

        Result<Service, ServiceError> validation = validateAndBuild(uuid, command);
        if (validation.isFailure()) {
            return validation;
        }

        return Result.success(serviceRepository.update(uuid, validation.getValue()));
    }

    @Override
    public Result<Service, ServiceError> deactivate(UUID uuid) {
        if (serviceRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new ServiceError.NotFound(uuid.toString()));
        }

        return Result.success(serviceRepository.deactivate(uuid));
    }

    @Override
    public Result<Service, ServiceError> activate(UUID uuid) {
        if (serviceRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new ServiceError.NotFound(uuid.toString()));
        }

        return Result.success(serviceRepository.activate(uuid));
    }

    private Result<Service, ServiceError> validateAndBuild(UUID uuid, ServiceCommand command) {
        boolean validCostConfiguration = command.consumptionBased()
            ? (command.unitCost() != null && command.cost() == null)
            : (command.cost() != null && command.unitCost() == null);
        if (!validCostConfiguration) {
            return Result.failure(new ServiceError.InvalidCostConfiguration());
        }

        Optional<RecurrenceType> recurrenceType = recurrenceTypeRepository.findByUuid(command.recurrenceTypeUuid());
        if (recurrenceType.isEmpty()) {
            return Result.failure(new ServiceError.RecurrenceTypeNotFound(command.recurrenceTypeUuid().toString()));
        }

        Optional<ChargeTargetType> chargeTargetType = chargeTargetTypeRepository.findByUuid(command.chargeTargetTypeUuid());
        if (chargeTargetType.isEmpty()) {
            return Result.failure(new ServiceError.ChargeTargetTypeNotFound(command.chargeTargetTypeUuid().toString()));
        }

        Optional<Currency> currency = currencyRepository.findByUuid(command.currencyUuid());
        if (currency.isEmpty()) {
            return Result.failure(new ServiceError.CurrencyNotFound(command.currencyUuid().toString()));
        }

        Service service = Service.builder()
            .uuid(uuid)
            .name(command.name())
            .recurrenceType(recurrenceType.get())
            .chargeTargetType(chargeTargetType.get())
            .currency(currency.get())
            .consumptionBased(command.consumptionBased())
            .cost(command.cost())
            .unitCost(command.unitCost())
            .build();
        return Result.success(service);
    }
}
