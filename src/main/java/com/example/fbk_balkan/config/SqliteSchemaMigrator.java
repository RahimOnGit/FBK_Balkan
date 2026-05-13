package com.example.fbk_balkan.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Database-aware schema migrator.
 *
 * Runs before DataInitializer (Order = MIN_VALUE) and adds any missing columns
 * or tables needed by new entities. Works against both SQLite (dev) and
 * PostgreSQL (prod) by using standard JDBC DatabaseMetaData for introspection
 * and branching DDL where the syntax differs.
 */
@Configuration
@Order(Integer.MIN_VALUE)
public class SqliteSchemaMigrator {

    private static final Logger log = LoggerFactory.getLogger(SqliteSchemaMigrator.class);

    private final DataSource dataSource;

    public SqliteSchemaMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void migrate() {
        try (Connection conn = dataSource.getConnection()) {

            boolean isPostgres = isPostgres(conn);
            log.info("[SqliteSchemaMigrator] Running against {} database",
                    isPostgres ? "PostgreSQL" : "SQLite");

            try (Statement stmt = conn.createStatement()) {

                // --- users table: add lockout columns if missing ---
                Set<String> userCols = listColumns(conn, "users");
                if (!userCols.isEmpty()) {
                    if (!userCols.contains("failed_login_attempts")) {
                        if (isPostgres) {
                            // On PostgreSQL: must supply a default so existing rows satisfy NOT NULL
                            stmt.execute(
                                    "ALTER TABLE users ADD COLUMN failed_login_attempts INTEGER NOT NULL DEFAULT 0"
                            );
                        } else {
                            stmt.execute(
                                    "ALTER TABLE users ADD COLUMN failed_login_attempts INTEGER DEFAULT 0"
                            );
                        }
                        log.info("[SqliteSchemaMigrator] Added users.failed_login_attempts");
                    }
                    if (!userCols.contains("locked_until")) {
                        stmt.execute("ALTER TABLE users ADD COLUMN locked_until TIMESTAMP");
                        log.info("[SqliteSchemaMigrator] Added users.locked_until");
                    }
                }

                // --- password_reset_tokens table ---
                if (isPostgres) {
                    stmt.execute("""
                        CREATE TABLE IF NOT EXISTS password_reset_tokens (
                            id         BIGSERIAL PRIMARY KEY,
                            token      VARCHAR(255) NOT NULL UNIQUE,
                            user_id    BIGINT NOT NULL,
                            expires_at TIMESTAMP NOT NULL,
                            used       BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at TIMESTAMP NOT NULL,
                            CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id)
                        )
                    """);
                } else {
                    stmt.execute("""
                        CREATE TABLE IF NOT EXISTS password_reset_tokens (
                            id         INTEGER PRIMARY KEY AUTOINCREMENT,
                            token      VARCHAR(255) NOT NULL UNIQUE,
                            user_id    INTEGER NOT NULL,
                            expires_at TIMESTAMP NOT NULL,
                            used       BOOLEAN NOT NULL DEFAULT 0,
                            created_at TIMESTAMP NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id)
                        )
                    """);
                }
            }

        } catch (Exception e) {
            log.error("[SqliteSchemaMigrator] Migration failed", e);
        }
    }

    /**
     * Uses standard JDBC DatabaseMetaData — works on both SQLite and PostgreSQL.
     * Returns column names in lower-case for case-insensitive comparison.
     */
    private Set<String> listColumns(Connection conn, String table) {
        Set<String> cols = new HashSet<>();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            // PostgreSQL stores names in lower-case; SQLite is case-insensitive.
            // Pass null for catalog/schema to search across all schemas.
            try (ResultSet rs = meta.getColumns(null, null, table, null)) {
                while (rs.next()) {
                    cols.add(rs.getString("COLUMN_NAME").toLowerCase());
                }
            }
            // If nothing found, try upper-case table name (some drivers need it)
            if (cols.isEmpty()) {
                try (ResultSet rs = meta.getColumns(null, null, table.toUpperCase(), null)) {
                    while (rs.next()) {
                        cols.add(rs.getString("COLUMN_NAME").toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[SqliteSchemaMigrator] Could not read columns of {}: {}", table, e.getMessage());
        }
        return cols;
    }

    private boolean isPostgres(Connection conn) {
        try {
            String name = conn.getMetaData().getDatabaseProductName();
            return name != null && name.toLowerCase().contains("postgresql");
        } catch (Exception e) {
            return false;
        }
    }
}