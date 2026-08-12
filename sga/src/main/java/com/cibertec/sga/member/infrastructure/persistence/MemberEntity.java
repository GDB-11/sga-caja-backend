package com.cibertec.sga.member.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "Member"}. {@code Uuid}/{@code CreatedAt}/{@code UpdatedAt} tienen
 * valor por defecto en la base de datos; Hibernate los recupera tras el insert.
 */
@Entity
@Table(name = "Member")
@EntityListeners(AuditingEntityListener.class)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "Code", nullable = false)
    private String code;

    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Column(name = "LastName", nullable = false)
    private String lastName;

    @Column(name = "ShareNumber")
    private String shareNumber;

    @Column(name = "StageId", nullable = false)
    private Long stageId;

    @Column(name = "BirthDate")
    private LocalDate birthDate;

    @Column(name = "IsActive", nullable = false)
    private boolean active;

    @CreatedBy
    @Column(name = "CreatedBy", nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "UpdatedBy", nullable = false)
    private Long updatedBy;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "UpdatedAt", nullable = false)
    private Instant updatedAt;

    protected MemberEntity() {
    }

    private MemberEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.code = builder.code;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.shareNumber = builder.shareNumber;
        this.stageId = builder.stageId;
        this.birthDate = builder.birthDate;
        this.active = builder.active;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(String shareNumber) {
        this.shareNumber = shareNumber;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private UUID uuid;
        private String code;
        private String firstName;
        private String lastName;
        private String shareNumber;
        private Long stageId;
        private LocalDate birthDate;
        private boolean active = true;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder shareNumber(String shareNumber) {
            this.shareNumber = shareNumber;
            return this;
        }

        public Builder stageId(Long stageId) {
            this.stageId = stageId;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public MemberEntity build() {
            return new MemberEntity(this);
        }
    }
}
