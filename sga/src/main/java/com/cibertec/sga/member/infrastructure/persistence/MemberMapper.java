package com.cibertec.sga.member.infrastructure.persistence;

import com.cibertec.sga.member.domain.model.Member;
import com.cibertec.sga.stage.domain.model.Stage;
import com.cibertec.sga.stage.infrastructure.persistence.StageJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link MemberEntity}/{@link MemberRow} (persistencia) y {@link Member} (modelo
 * de dominio). Resuelve el {@code StageId} interno a partir del {@code Uuid} de la etapa del
 * modelo de dominio vía {@link StageJpaRepository} (dependencia infra-a-infra, no atraviesa la
 * capa de dominio) — el modelo de dominio nunca conoce Ids internos.
 */
@Component
public class MemberMapper {

    private final StageJpaRepository stageJpaRepository;

    public MemberMapper(StageJpaRepository stageJpaRepository) {
        this.stageJpaRepository = stageJpaRepository;
    }

    public Member toDomain(MemberRow row) {
        Stage stage = Stage.builder()
            .uuid(row.getStageUuid())
            .code(row.getStageCode())
            .name(row.getStageName())
            .build();
        return Member.builder()
            .uuid(row.getUuid())
            .code(row.getCode())
            .firstName(row.getFirstName())
            .lastName(row.getLastName())
            .shareNumber(row.getShareNumber())
            .stage(stage)
            .birthDate(row.getBirthDate())
            .active(row.getIsActive())
            .build();
    }

    public MemberEntity toNewEntity(Member member) {
        return MemberEntity.builder()
            .code(member.getCode())
            .firstName(member.getFirstName())
            .lastName(member.getLastName())
            .shareNumber(member.getShareNumber())
            .stageId(resolveStageId(member))
            .birthDate(member.getBirthDate())
            .build();
    }

    public void updateEntity(MemberEntity entity, Member member) {
        entity.setCode(member.getCode());
        entity.setFirstName(member.getFirstName());
        entity.setLastName(member.getLastName());
        entity.setShareNumber(member.getShareNumber());
        entity.setStageId(resolveStageId(member));
        entity.setBirthDate(member.getBirthDate());
    }

    private Long resolveStageId(Member member) {
        return stageJpaRepository.findByUuid(member.getStage().getUuid()).orElseThrow().getId();
    }
}
