package com.cibertec.sga.common;

import java.nio.file.Path;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

/**
 * Base para tests de integración {@code @SpringBootTest}: levanta un Postgres efímero
 * (Testcontainers) y le aplica el esquema real de {@code sga-caja-db} antes de que arranque
 * el contexto de Spring, en vez de usar {@code ddl-auto} o un esquema de prueba paralelo.
 *
 * <p>Asume que el repo {@code sga-caja-db} está clonado como hermano de este repo
 * ({@code ../../sga-caja-db} relativo al módulo Maven {@code sga/}) — ajustar la resolución
 * de {@code MIGRATIONS_DIR} si esa disposición de carpetas cambia, o cuando se configure CI
 * con checkout de ambos repos.
 */
@Testcontainers
@SpringBootTest
public abstract class AbstractIntegrationTest {

    private static final Path MIGRATIONS_DIR =
        Path.of(System.getProperty("user.dir"), "..", "..", "sga-caja-db", "migrations").normalize();

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18")
        .withCopyFileToContainer(MountableFile.forHostPath(MIGRATIONS_DIR), "/migrations")
        .withCopyFileToContainer(
            MountableFile.forClasspathResource("db/run-migrations-init.sql"),
            "/docker-entrypoint-initdb.d/001-run-migrations.sql"
        );
}
