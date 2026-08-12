package com.cibertec.sga.common;

import java.nio.file.Path;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

/**
 * Base para tests de integración {@code @SpringBootTest}: levanta un Postgres efímero
 * (Testcontainers) y le aplica el esquema real de {@code sga-caja-db} antes de que arranque
 * el contexto de Spring, en vez de usar {@code ddl-auto} o un esquema de prueba paralelo.
 *
 * <p>Patrón "singleton container" (deliberado, no {@code @Testcontainers}/{@code @Container}):
 * el contenedor arranca una única vez en el inicializador estático de esta clase y vive el resto
 * del proceso de test. Con {@code @Testcontainers}/{@code @Container} cada clase de test para/
 * reinicia el contenedor por su cuenta (ciclo de vida por clase), pero Spring reutiliza el mismo
 * {@code ApplicationContext}/{@code DataSource} cacheado entre clases estructuralmente iguales
 * (ej. {@code BusinessTypeIntegrationTest} y {@code AuthIntegrationTest}) — la combinación deja
 * al pool de conexiones apuntando a un contenedor ya muerto en cuanto arranca la segunda clase
 * (visto en la práctica: "HikariPool-2 - Connection is not available" apenas empieza la 2ª clase
 * que comparte el context). Un único contenedor para todo el proceso evita el problema de raíz.
 *
 * <p>Asume que el repo {@code sga-caja-db} está clonado como hermano de este repo
 * ({@code ../../sga-caja-db} relativo al módulo Maven {@code sga/}) — ajustar la resolución
 * de {@code SGA_CAJA_DB_DIR} si esa disposición de carpetas cambia, o cuando se configure CI
 * con checkout de ambos repos. También aplica {@code seed/dev_seed.sql} (usuarios de prueba
 * admin/cashier) tras las migraciones, para que los tests de auth tengan credenciales reales.
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {

    private static final Path SGA_CAJA_DB_DIR =
        Path.of(System.getProperty("user.dir"), "..", "..", "sga-caja-db").normalize();

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18")
        .withCopyFileToContainer(MountableFile.forHostPath(SGA_CAJA_DB_DIR.resolve("migrations")), "/migrations")
        .withCopyFileToContainer(MountableFile.forHostPath(SGA_CAJA_DB_DIR.resolve("seed")), "/seed")
        .withCopyFileToContainer(
            MountableFile.forClasspathResource("db/run-migrations-init.sql"),
            "/docker-entrypoint-initdb.d/001-run-migrations.sql"
        );

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
