package com.cibertec.sga.payment.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code PaymentDetail}. Solo {@code saveAll} heredado y una
 * consulta propia con {@code @Query(nativeQuery = true)}.
 */
public interface PaymentDetailJpaRepository extends JpaRepository<PaymentDetailEntity, Long> {

    @Query(nativeQuery = true, value = """
        SELECT ar."Uuid" AS account_receivable_uuid, pd."Amount" AS amount
        FROM "PaymentDetail" pd
        JOIN "AccountReceivable" ar ON ar."Id" = pd."AccountReceivableId"
        WHERE pd."PaymentId" = :paymentId
        """)
    List<PaymentDetailRow> findRowsByPaymentId(@Param("paymentId") Long paymentId);
}
