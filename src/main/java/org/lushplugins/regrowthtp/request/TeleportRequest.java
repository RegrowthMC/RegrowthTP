package org.lushplugins.regrowthtp.request;

import org.lushplugins.regrowthtp.RegrowthTP;

import java.util.UUID;

public class TeleportRequest {
    private final UUID from;
    private final UUID to;
    private final TeleportDirection direction;
    private final long expiryTime;

    public TeleportRequest(UUID from, UUID to, TeleportDirection direction) {
        this.from = from;
        this.to = to;
        this.direction = direction;
        this.expiryTime = System.currentTimeMillis() + RegrowthTP.getInstance().getConfigManager().getRequestExpiryTime();
    }

    public UUID from() {
        return from;
    }

    public UUID to() {
        return to;
    }

    public TeleportDirection direction() {
        return direction;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    public long expiryTime() {
        return expiryTime;
    }
}
