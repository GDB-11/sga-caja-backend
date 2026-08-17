package com.cibertec.sga.provider.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.provider.domain.error.ProviderError;
import com.cibertec.sga.provider.domain.model.Provider;
import com.cibertec.sga.provider.domain.repository.IProviderRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProviderService implements IProviderService {

    private final IProviderRepository providerRepository;

    public ProviderService(IProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public Page<Provider> search(String search, Boolean active, Pageable pageable) {
        return providerRepository.search(search, active, pageable);
    }

    @Override
    public Result<Provider, ProviderError> findByUuid(UUID uuid) {
        return providerRepository.findByUuid(uuid)
            .map(Result::<Provider, ProviderError>success)
            .orElseGet(() -> Result.failure(new ProviderError.NotFound(uuid.toString())));
    }

    @Override
    public Result<Provider, ProviderError> create(ProviderCommand command) {
        Provider provider = Provider.builder()
            .name(command.name())
            .document(command.document())
            .build();
        return Result.success(providerRepository.insert(provider));
    }

    @Override
    public Result<Provider, ProviderError> update(UUID uuid, ProviderCommand command) {
        if (providerRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new ProviderError.NotFound(uuid.toString()));
        }

        Provider provider = Provider.builder()
            .uuid(uuid)
            .name(command.name())
            .document(command.document())
            .build();
        return Result.success(providerRepository.update(uuid, provider));
    }

    @Override
    public Result<Provider, ProviderError> deactivate(UUID uuid) {
        if (providerRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new ProviderError.NotFound(uuid.toString()));
        }

        return Result.success(providerRepository.deactivate(uuid));
    }

    @Override
    public Result<Provider, ProviderError> activate(UUID uuid) {
        if (providerRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new ProviderError.NotFound(uuid.toString()));
        }

        return Result.success(providerRepository.activate(uuid));
    }
}
