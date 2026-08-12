package com.cibertec.sga.user.infrastructure.persistence;

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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "User"}. {@code Uuid}/{@code CreatedAt}/{@code UpdatedAt} tienen valor
 * por defecto en la base de datos; Hibernate los recupera tras el insert.
 */
@Entity
@Table(name = "User")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "Username", nullable = false)
    private String username;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Column(name = "LastName", nullable = false)
    private String lastName;

    @Column(name = "RoleId", nullable = false)
    private Long roleId;

    @Column(name = "IsActive", nullable = false)
    private boolean active;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "UpdatedAt", nullable = false)
    private Instant updatedAt;

    protected UserEntity() {
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public boolean isActive() {
        return active;
    }
}
