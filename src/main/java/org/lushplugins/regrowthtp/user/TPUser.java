package org.lushplugins.regrowthtp.user;

import org.jooq.impl.DSL;
import org.lushplugins.regrowthtp.RegrowthTP;

import java.util.UUID;

public class TPUser {
    private final UUID uuid;
    private boolean requestsEnabled;

    public TPUser(
        UUID uuid,
        boolean requestsEnabled
    ) {
        this.uuid = uuid;
        this.requestsEnabled = requestsEnabled;
    }

    public TPUser(UUID uuid) {
        this(uuid, true);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public boolean areRequestsEnabled() {
        return requestsEnabled;
    }

    public void setRequestsEnabled(boolean requestsEnabled) {
        this.requestsEnabled = requestsEnabled;
        save();
    }

    public void save() {
        RegrowthTP.getInstance().getStorageManager().execute(context -> context
            .insertInto(DSL.table("regrowthtp_users"))
            .set(DSL.field("uuid"), uuid)
            .set(DSL.field("requests_enabled"), requestsEnabled)
            .onDuplicateKeyUpdate()
            .set(DSL.field("uuid"), uuid)
            .set(DSL.field("requests_enabled"), requestsEnabled)
            .execute()
        );
    }
}
