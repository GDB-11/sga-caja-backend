package com.cibertec.sga.stage.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.stage.domain.error.StageError;
import com.cibertec.sga.stage.domain.model.Stage;
import com.cibertec.sga.stage.domain.repository.IStageRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class StageService implements IStageService {

    private final IStageRepository stageRepository;

    public StageService(IStageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Override
    public List<Stage> findAll() {
        return stageRepository.findAll();
    }

    @Override
    public Result<Stage, StageError> findByUuid(UUID uuid) {
        return stageRepository.findByUuid(uuid)
            .map(Result::<Stage, StageError>success)
            .orElseGet(() -> Result.failure(new StageError.NotFound(uuid.toString())));
    }
}
