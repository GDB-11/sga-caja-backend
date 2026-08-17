package com.cibertec.sga.member.infrastructure.persistence;

import com.cibertec.sga.member.domain.model.Member;
import com.cibertec.sga.member.domain.repository.IMemberRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository implements IMemberRepository {

    private final MemberJpaRepository jpaRepository;
    private final MemberMapper mapper;

    public MemberRepository(MemberJpaRepository jpaRepository, MemberMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Member> search(String search, Boolean active, Pageable pageable) {
        return jpaRepository.search(search, active, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<Member> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public List<Member> findAllActiveByStageCodes(List<Short> stageCodes) {
        return jpaRepository.findAllActiveByStageCodesRows(stageCodes).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCode(String memberCode) {
        return jpaRepository.existsByCode(memberCode);
    }

    @Override
    public boolean existsByCodeAndUuidNot(String memberCode, UUID uuid) {
        return jpaRepository.existsByCodeAndUuidNot(memberCode, uuid);
    }

    @Override
    public Member insert(Member member) {
        MemberEntity saved = jpaRepository.save(mapper.toNewEntity(member));
        return findByUuid(saved.getUuid()).orElseThrow();
    }

    @Override
    public Member update(UUID uuid, Member member) {
        MemberEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        mapper.updateEntity(entity, member);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }

    @Override
    public Member deactivate(UUID uuid) {
        MemberEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setActive(false);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }

    @Override
    public Member activate(UUID uuid) {
        MemberEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setActive(true);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }
}
