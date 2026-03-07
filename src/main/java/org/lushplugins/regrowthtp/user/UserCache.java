package org.lushplugins.regrowthtp.user;

import org.bukkit.plugin.java.JavaPlugin;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.lushplugins.regrowthtp.RegrowthTP;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserCache extends org.lushplugins.lushlib.cache.UserCache<TPUser> {

    public UserCache(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected CompletableFuture<TPUser> load(UUID uuid) {
        return RegrowthTP.getInstance().getStorageManager().query((context) -> {
             Record result = context.select()
                .from(DSL.table("regrowthtp_users"))
                .where(DSL.field("uuid").eq(uuid))
                .fetchOne();
             if (result == null) {
                 return new TPUser(uuid);
             }

             boolean requestsEnabled = result.getValue(DSL.field("requests_enabled", Boolean.class));
             return new TPUser(uuid, requestsEnabled);
        });
    }
}
