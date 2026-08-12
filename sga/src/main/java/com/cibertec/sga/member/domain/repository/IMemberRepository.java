package com.cibertec.sga.member.domain.repository;

import com.cibertec.sga.member.domain.model.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link Member}, implementado en {@code infrastructure}.
 */
public interface IMemberRepository {

    Page<Member> search(String search, Boolean active, Pageable pageable);

    Optional<Member> findByUuid(UUID uuid);

    boolean existsByCode(String memberCode);

    boolean existsByCodeAndUuidNot(String memberCode, UUID uuid);

    Member insert(Member member);

    Member update(UUID uuid, Member member);

    Member deactivate(UUID uuid);
}
