package com.cibertec.sga.expense.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

/**
 * Fila de la tabla {@code "ExpenseBulkUploadStatus"}. Catálogo de solo lectura sembrado por
 * migración (Received/Processed/Failed), usado únicamente para resolver el {@code Id} interno
 * al crear un {@code ExpenseBulkUpload} — a diferencia de {@code ExpenseStatus}/{@code
 * ExpenseReason}, no se expone como catálogo público porque el usuario nunca lo selecciona (lo
 * asigna el sistema al procesar la carga masiva, RF-28).
 */
@Entity
@Table(name = "ExpenseBulkUploadStatus")
public class ExpenseBulkUploadStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "Name", nullable = false)
    private String name;

    protected ExpenseBulkUploadStatusEntity() {
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
