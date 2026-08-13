package com.cibertec.sga.expense.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "ExpenseBulkUpload"}: lote de carga masiva de egresos (RF-28).
 * {@code Uuid} tiene valor por defecto en la base de datos.
 */
@Entity
@Table(name = "ExpenseBulkUpload")
@EntityListeners(AuditingEntityListener.class)
public class ExpenseBulkUploadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "FileName", nullable = false)
    private String fileName;

    @Column(name = "ExpenseBulkUploadStatusId", nullable = false)
    private Long expenseBulkUploadStatusId;

    @CreatedBy
    @Column(name = "CreatedBy", nullable = false, updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    protected ExpenseBulkUploadEntity() {
    }

    private ExpenseBulkUploadEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.fileName = builder.fileName;
        this.expenseBulkUploadStatusId = builder.expenseBulkUploadStatusId;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getExpenseBulkUploadStatusId() {
        return expenseBulkUploadStatusId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private UUID uuid;
        private String fileName;
        private Long expenseBulkUploadStatusId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder expenseBulkUploadStatusId(Long expenseBulkUploadStatusId) {
            this.expenseBulkUploadStatusId = expenseBulkUploadStatusId;
            return this;
        }

        public ExpenseBulkUploadEntity build() {
            return new ExpenseBulkUploadEntity(this);
        }
    }
}
