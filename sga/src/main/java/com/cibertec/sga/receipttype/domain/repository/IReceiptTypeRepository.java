package com.cibertec.sga.receipttype.domain.repository;

import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link ReceiptType}, implementado en {@code infrastructure}.
 */
public interface IReceiptTypeRepository {

    List<ReceiptType> findAll();

    Optional<ReceiptType> findByUuid(UUID uuid);

    Optional<ReceiptType> findByName(String name);
}
