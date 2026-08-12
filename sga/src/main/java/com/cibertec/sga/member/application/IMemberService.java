package com.cibertec.sga.member.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.member.domain.error.MemberError;
import com.cibertec.sga.member.domain.model.Member;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code Member} (RF-05–RF-07): listar (búsqueda + paginación), obtener,
 * crear, editar y desactivar socios. Es la única interfaz que se inyecta en
 * {@code MemberController}.
 */
public interface IMemberService {

    Page<Member> search(String search, Boolean active, Pageable pageable);

    Result<Member, MemberError> findByUuid(UUID uuid);

    Result<Member, MemberError> create(MemberCommand command);

    Result<Member, MemberError> update(UUID uuid, MemberCommand command);

    Result<Member, MemberError> deactivate(UUID uuid);
}
