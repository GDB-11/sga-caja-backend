package com.cibertec.sga.stall.infrastructure.persistence;

import com.cibertec.sga.businesstype.domain.model.BusinessType;
import com.cibertec.sga.businesstype.infrastructure.persistence.BusinessTypeJpaRepository;
import com.cibertec.sga.member.infrastructure.persistence.MemberJpaRepository;
import com.cibertec.sga.stall.domain.model.MemberSummary;
import com.cibertec.sga.stall.domain.model.Stall;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link StallEntity}/{@link StallRow} (persistencia) y {@link Stall} (modelo de
 * dominio). Resuelve {@code BusinessTypeId}/{@code MemberId} internos a partir de los
 * {@code Uuid} del modelo de dominio vía los JPA repositories de esos módulos (dependencia
 * infra-a-infra) — el modelo de dominio nunca conoce Ids internos.
 */
@Component
public class StallMapper {

    private final BusinessTypeJpaRepository businessTypeJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    public StallMapper(BusinessTypeJpaRepository businessTypeJpaRepository, MemberJpaRepository memberJpaRepository) {
        this.businessTypeJpaRepository = businessTypeJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    public Stall toDomain(StallRow row) {
        BusinessType businessType = BusinessType.builder()
            .uuid(row.getBusinessTypeUuid())
            .name(row.getBusinessTypeName())
            .build();
        MemberSummary member = row.getMemberUuid() == null
            ? null
            : new MemberSummary(row.getMemberUuid(), row.getMemberFullName());
        return Stall.builder()
            .uuid(row.getUuid())
            .number(row.getNumber())
            .businessType(businessType)
            .member(member)
            .tenantName(row.getTenantName())
            .tenantDocument(row.getTenantDocument())
            .validityStartDate(row.getValidityStartDate())
            .validityEndDate(row.getValidityEndDate())
            .active(row.getIsActive())
            .build();
    }

    public StallEntity toNewEntity(Stall stall) {
        return StallEntity.builder()
            .number(stall.getNumber())
            .businessTypeId(resolveBusinessTypeId(stall))
            .memberId(resolveMemberId(stall))
            .tenantName(stall.getTenantName())
            .tenantDocument(stall.getTenantDocument())
            .validityStartDate(stall.getValidityStartDate())
            .validityEndDate(stall.getValidityEndDate())
            .build();
    }

    public void updateEntity(StallEntity entity, Stall stall) {
        entity.setNumber(stall.getNumber());
        entity.setBusinessTypeId(resolveBusinessTypeId(stall));
        entity.setMemberId(resolveMemberId(stall));
        entity.setTenantName(stall.getTenantName());
        entity.setTenantDocument(stall.getTenantDocument());
        entity.setValidityStartDate(stall.getValidityStartDate());
        entity.setValidityEndDate(stall.getValidityEndDate());
    }

    private Long resolveBusinessTypeId(Stall stall) {
        return businessTypeJpaRepository.findByUuid(stall.getBusinessType().getUuid()).orElseThrow().getId();
    }

    private Long resolveMemberId(Stall stall) {
        if (stall.getMember() == null) {
            return null;
        }
        return memberJpaRepository.findEntityByUuid(stall.getMember().uuid()).orElseThrow().getId();
    }
}
