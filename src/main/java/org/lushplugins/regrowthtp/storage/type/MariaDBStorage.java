package org.lushplugins.regrowthtp.storage.type;

import org.jooq.SQLDialect;

public class MariaDBStorage extends MySQLStorage {

    @Override
    public SQLDialect dialect() {
        return SQLDialect.MARIADB;
    }
}
