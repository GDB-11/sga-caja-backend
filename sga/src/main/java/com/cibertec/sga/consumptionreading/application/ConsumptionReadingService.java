package com.cibertec.sga.consumptionreading.application;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.repository.IAccountReceivableRepository;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.consumptionreading.domain.error.ConsumptionReadingError;
import com.cibertec.sga.consumptionreading.domain.model.ConsumptionReading;
import com.cibertec.sga.consumptionreading.domain.repository.IConsumptionReadingRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ConsumptionReadingService implements IConsumptionReadingService {

    private final IConsumptionReadingRepository consumptionReadingRepository;
    private final IAccountReceivableRepository accountReceivableRepository;

    public ConsumptionReadingService(
        IConsumptionReadingRepository consumptionReadingRepository, IAccountReceivableRepository accountReceivableRepository
    ) {
        this.consumptionReadingRepository = consumptionReadingRepository;
        this.accountReceivableRepository = accountReceivableRepository;
    }

    @Override
    public Result<ConsumptionReading, ConsumptionReadingError> register(RegisterConsumptionReadingCommand command) {
        var accountReceivableOpt = accountReceivableRepository.findByUuid(command.accountReceivableUuid());
        if (accountReceivableOpt.isEmpty()) {
            return Result.failure(new ConsumptionReadingError.AccountReceivableNotFound(command.accountReceivableUuid().toString()));
        }
        AccountReceivable accountReceivable = accountReceivableOpt.get();

        if (!accountReceivable.getService().isConsumptionBased()) {
            return Result.failure(new ConsumptionReadingError.ServiceNotConsumptionBased(command.accountReceivableUuid().toString()));
        }

        if (consumptionReadingRepository.existsByAccountReceivableUuid(command.accountReceivableUuid())) {
            return Result.failure(new ConsumptionReadingError.DuplicateReading(command.accountReceivableUuid().toString()));
        }

        ConsumptionReading toInsert = ConsumptionReading.builder()
            .accountReceivableUuid(command.accountReceivableUuid())
            .initialReading(command.initialReading())
            .finalReading(command.finalReading())
            .unitCost(accountReceivable.getService().getUnitCost())
            .build();

        ConsumptionReading saved = consumptionReadingRepository.insert(toInsert);

        accountReceivableRepository.updateAmount(command.accountReceivableUuid(), saved.getCalculatedAmount());

        return Result.success(saved);
    }

    @Override
    public Result<ConsumptionReading, ConsumptionReadingError> findByUuid(UUID uuid) {
        return consumptionReadingRepository.findByUuid(uuid)
            .map(Result::<ConsumptionReading, ConsumptionReadingError>success)
            .orElseGet(() -> Result.failure(new ConsumptionReadingError.NotFound(uuid.toString())));
    }

    @Override
    public Result<ConsumptionReading, ConsumptionReadingError> findByAccountReceivableUuid(UUID accountReceivableUuid) {
        return consumptionReadingRepository.findByAccountReceivableUuid(accountReceivableUuid)
            .map(Result::<ConsumptionReading, ConsumptionReadingError>success)
            .orElseGet(() -> Result.failure(new ConsumptionReadingError.NotFound(accountReceivableUuid.toString())));
    }
}
