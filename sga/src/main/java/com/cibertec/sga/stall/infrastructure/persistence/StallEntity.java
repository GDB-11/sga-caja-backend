package com.cibertec.sga.stall.infrastructure.persistence;

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
 * Fila de la tabla {@code "Stall"}. {@code Uuid}/{@code CreatedAt}/{@code UpdatedAt} tienen
 * valor por defecto en la base de datos; Hibernate los recupera tras el insert.
 */
@Entity
@Table(name = "Stall")
@EntityListeners(AuditingEntityListener.class)
public class StallEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "Number", nullable = false)
    private String number;

    @Column(name = "MemberId")
    private Long memberId;

    @Column(name = "BusinessTypeId", nullable = false)
    private Long businessTypeId;

    @Column(name = "TenantName")
    private String tenantName;

    @Column(name = "TenantDocument")
    private String tenantDocument;

    @Column(name = "ValidityStartDate")
    private LocalDate validityStartDate;

    @Column(name = "ValidityEndDate")
    private LocalDate validityEndDate;

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

    protected StallEntity() {
    }

    private StallEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.number = builder.number;
        this.memberId = builder.memberId;
        this.businessTypeId = builder.businessTypeId;
        this.tenantName = builder.tenantName;
        this.tenantDocument = builder.tenantDocument;
        this.validityStartDate = builder.validityStartDate;
        this.validityEndDate = builder.validityEndDate;
        this.active = builder.active;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(Long businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantDocument() {
        return tenantDocument;
    }

    public void setTenantDocument(String tenantDocument) {
        this.tenantDocument = tenantDocument;
    }

    public LocalDate getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(LocalDate validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public LocalDate getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(LocalDate validityEndDate) {
        this.validityEndDate = validityEndDate;
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
        private String number;
        private Long memberId;
        private Long businessTypeId;
        private String tenantName;
        private String tenantDocument;
        private LocalDate validityStartDate;
        private LocalDate validityEndDate;
        private boolean active = true;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder memberId(Long memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder businessTypeId(Long businessTypeId) {
            this.businessTypeId = businessTypeId;
            return this;
        }

        public Builder tenantName(String tenantName) {
            this.tenantName = tenantName;
            return this;
        }

        public Builder tenantDocument(String tenantDocument) {
            this.tenantDocument = tenantDocument;
            return this;
        }

        public Builder validityStartDate(LocalDate validityStartDate) {
            this.validityStartDate = validityStartDate;
            return this;
        }

        public Builder validityEndDate(LocalDate validityEndDate) {
            this.validityEndDate = validityEndDate;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public StallEntity build() {
            return new StallEntity(this);
        }
    }
}
