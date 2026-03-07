package org.lushplugins.regrowthtp.request;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.UUID;

public class RequestManager {
    private final Multimap<UUID, TeleportRequest> requests = LinkedHashMultimap.create();

    public void validateRequests(UUID to) {
        this.requests.get(to).removeIf(request -> request.hasExpired() || Bukkit.getPlayer(request.from()) == null);
    }

    public TeleportRequest findRequest(UUID from, UUID to) {
        validateRequests(to);
        return this.requests.get(to).stream()
            .filter(request -> request.from() == from)
            .findFirst()
            .orElse(null);
    }

    public TeleportRequest findMostRecentRequest(UUID to) {
        validateRequests(to);
        Collection<TeleportRequest> requests = this.requests.get(to);
        return requests.isEmpty() ? null : requests.iterator().next();
    }

    public void sendRequest(TeleportRequest request) {
        validateRequests(request.to());
        this.requests.put(request.to(), request);
    }

    public void removeRequest(TeleportRequest request) {
        this.requests.remove(request.to(), request);
    }

    public void removeRequest(UUID from, UUID to) {
        TeleportRequest request = findRequest(from, to);
        if (request != null) {
            removeRequest(request);
        }
    }

    public void clearRequests(UUID to) {
        this.requests.removeAll(to);
    }
}
