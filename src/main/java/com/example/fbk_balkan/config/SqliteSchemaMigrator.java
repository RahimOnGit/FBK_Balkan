package com.example.fbk_balkan.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Hibernate's SQLite dialect does not reliably perform ALTER TABLE ADD COLUMN
 * with ddl-auto=update. This migrator runs after the DataSource is ready and
 * adds any missing columns / tables required by new entities.
 *
 * It runs early (Order = highest precedence) so it executes before
 * DataInitializer (which queries entities).
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
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // --- users table: add lockout columns if missing ---
            Set<String> userCols = listColumns(conn, "users");
            if (!userCols.isEmpty()) {
                if (!userCols.contains("failed_login_attempts")) {
                    stmt.execute("ALTER TABLE users ADD COLUMN failed_login_attempts INTEGER DEFAULT 0");
                    log.info("[SqliteSchemaMigrator] Added users.failed_login_attempts");
                }
                if (!userCols.contains("locked_until")) {
                    stmt.execute("ALTER TABLE users ADD COLUMN locked_until TIMESTAMP");
                    log.info("[SqliteSchemaMigrator] Added users.locked_until");
                }
            }

            // --- password_reset_tokens table (in case Hibernate hasn't created it) ---
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS password_reset_tokens (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    token VARCHAR(255) NOT NULL UNIQUE,
                    user_id INTEGER NOT NULL,
                    expires_at TIMESTAMP NOT NULL,
                    used BOOLEAN NOT NULL DEFAULT 0,
                    created_at TIMESTAMP NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
        } catch (Exception e) {
            log.error("[SqliteSchemaMigrator] Migration failed", e);
        }
    }

    private Set<String> listColumns(Connection conn, String table) {
        Set<String> cols = new HashSet<>();
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                cols.add(rs.getString("name").toLowerCase());
            }
        } catch (Exception e) {
            log.warn("[SqliteSchemaMigrator] Could not read columns of {}: {}", table, e.getMessage());
        }
        return cols;
    }
}
