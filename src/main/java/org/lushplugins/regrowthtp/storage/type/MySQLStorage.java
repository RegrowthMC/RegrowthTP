package org.lushplugins.regrowthtp.storage.type;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.jooq.SQLDialect;
import org.lushplugins.regrowthtp.storage.Storage;

import javax.sql.DataSource;

public class MySQLStorage extends Storage {

    @Override
    public SQLDialect dialect() {
        return SQLDialect.MYSQL;
    }

    @Override
    public DataSource setupDataSource(ConfigurationSection config) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(config.getString("host"));
        dataSource.setPortNumber(config.getInt("port"));
        dataSource.setDatabaseName(config.getString("database"));
        dataSource.setUser(config.getString("username"));
        dataSource.setPassword(config.getString("password"));

        return dataSource;
    }
}
