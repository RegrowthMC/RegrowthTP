package org.lushplugins.regrowthtp.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.jooq.SQLDialect;
import org.jooq.impl.SQLDataType;
import org.lushplugins.regrowthtp.RegrowthTP;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Storage {
    private DataSource dataSource;

    public abstract SQLDialect dialect();

    public void setup(ConfigurationSection config) {
        this.dataSource = setupDataSource(config);
        test();

        RegrowthTP.getInstance().getStorageManager().execute(context -> context
            .createTableIfNotExists("regrowthtp_users")
            .column("uuid", SQLDataType.UUID.notNull())
            .column("requests_enabled", SQLDataType.BOOLEAN)
            .primaryKey("uuid")
            .execute()
        );
    }

    public abstract DataSource setupDataSource(ConfigurationSection config);

    public Connection conn() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void test() {
        try (Connection conn = conn()) {
            if (!conn.isValid(30)) {
                throw new SQLException("Could not establish database connection.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
    }
}
