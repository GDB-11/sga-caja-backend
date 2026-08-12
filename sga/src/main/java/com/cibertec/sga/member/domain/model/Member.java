package com.cibertec.sga.member.domain.model;

import com.cibertec.sga.stage.domain.model.Stage;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Socio: persona asociada a la organización, con código, nombres, apellidos, acción y etapa
 * (RF-05–RF-07).
 */
public final class Member {

    private final UUID uuid;
    private final String code;
    private final String firstName;
    private final String lastName;
    private final String shareNumber;
    private final Stage stage;
    private final LocalDate birthDate;
    private final boolean active;

    private Member(Builder builder) {
        this.uuid = builder.uuid;
        this.code = builder.code;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.shareNumber = builder.shareNumber;
        this.stage = builder.stage;
        this.birthDate = builder.birthDate;
        this.active = builder.active;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCode() {
        return code;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getShareNumber() {
        return shareNumber;
    }

    public Stage getStage() {
        return stage;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public boolean isActive() {
        return active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private String code;
        private String firstName;
        private String lastName;
        private String shareNumber;
        private Stage stage;
        private LocalDate birthDate;
        private boolean active = true;

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

        public Builder stage(Stage stage) {
            this.stage = stage;
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

        public Member build() {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("El código del socio es obligatorio");
            }
            if (firstName == null || firstName.isBlank()) {
                throw new IllegalArgumentException("El nombre del socio es obligatorio");
            }
            if (lastName == null || lastName.isBlank()) {
                throw new IllegalArgumentException("El apellido del socio es obligatorio");
            }
            if (stage == null) {
                throw new IllegalArgumentException("La etapa del socio es obligatoria");
            }
            return new Member(this);
        }
    }
}
