package com.cibertec.sga.stall.domain.model;

import com.cibertec.sga.businesstype.domain.model.BusinessType;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Puesto: unidad o local asociado a un giro comercial y, cuando corresponda, a un socio
 * (RF-09–RF-11, RN-01).
 */
public final class Stall {

    private final UUID uuid;
    private final String number;
    private final BusinessType businessType;
    private final MemberSummary member;
    private final String tenantName;
    private final String tenantDocument;
    private final LocalDate validityStartDate;
    private final LocalDate validityEndDate;
    private final boolean active;

    private Stall(Builder builder) {
        this.uuid = builder.uuid;
        this.number = builder.number;
        this.businessType = builder.businessType;
        this.member = builder.member;
        this.tenantName = builder.tenantName;
        this.tenantDocument = builder.tenantDocument;
        this.validityStartDate = builder.validityStartDate;
        this.validityEndDate = builder.validityEndDate;
        this.active = builder.active;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNumber() {
        return number;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public MemberSummary getMember() {
        return member;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getTenantDocument() {
        return tenantDocument;
    }

    public LocalDate getValidityStartDate() {
        return validityStartDate;
    }

    public LocalDate getValidityEndDate() {
        return validityEndDate;
    }

    public boolean isActive() {
        return active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private String number;
        private BusinessType businessType;
        private MemberSummary member;
        private String tenantName;
        private String tenantDocument;
        private LocalDate validityStartDate;
        private LocalDate validityEndDate;
        private boolean active = true;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder businessType(BusinessType businessType) {
            this.businessType = businessType;
            return this;
        }

        public Builder member(MemberSummary member) {
            this.member = member;
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

        public Stall build() {
            if (number == null || number.isBlank()) {
                throw new IllegalArgumentException("El número de puesto es obligatorio");
            }
            if (businessType == null) {
                throw new IllegalArgumentException("El giro comercial del puesto es obligatorio");
            }
            if (validityStartDate != null && validityEndDate != null && validityEndDate.isBefore(validityStartDate)) {
                throw new IllegalArgumentException("El período de vigencia del puesto es inválido");
            }
            return new Stall(this);
        }
    }
}
