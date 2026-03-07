package org.lushplugins.regrowthtp.storage.type;

import org.bukkit.configuration.ConfigurationSection;
import org.jooq.SQLDialect;
import org.lushplugins.regrowthtp.storage.Storage;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteStorage extends Storage {
    private final Path dbPath;

    public SQLiteStorage(Path dbPath) {
        this.dbPath = dbPath.toAbsolutePath().normalize();
    }

    @Override
    public SQLDialect dialect() {
        return SQLDialect.SQLITE;
    }

    @Override
    public DataSource setupDataSource(ConfigurationSection config) {
        return null;
    }

    @Override
    public Connection conn() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
