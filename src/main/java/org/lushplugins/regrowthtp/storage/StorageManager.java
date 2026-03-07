package org.lushplugins.regrowthtp.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.lushplugins.regrowthtp.RegrowthTP;
import org.lushplugins.regrowthtp.storage.type.MariaDBStorage;
import org.lushplugins.regrowthtp.storage.type.MySQLStorage;
import org.lushplugins.regrowthtp.storage.type.SQLiteStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class StorageManager {
    private final ExecutorService threads = Executors.newFixedThreadPool(1);
    private Storage storage;

    public StorageManager() {
        RegrowthTP.getInstance().saveDefaultResource("storage.yml");
    }

    public void shutdown() {
        if (storage != null) {
            runAsync(storage::shutdown);
        }
    }

    public void reload() {
        shutdown();

        FileConfiguration config = RegrowthTP.getInstance().getConfigResource("storage.yml");
        String storageType = config.getString("type");
        if (storageType == null) {
            RegrowthTP.getInstance().getLogger().severe("No storage type is defined");
            return;
        }

        switch (storageType) {
            case "mysql" -> storage = new MySQLStorage();
            case "mariadb" -> storage = new MariaDBStorage();
            case "sqlite" -> storage = new SQLiteStorage(RegrowthTP.getInstance().getDataPath().resolve("data.db"));
            default -> {
                RegrowthTP.getInstance().getLogger().severe(String.format("'%s' is not a valid storage type", storageType));
                return;
            }
        }

        runAsync(() -> storage.setup(config));
    }

    public <T> CompletableFuture<T> query(Function<DSLContext, T> function) {
        return runAsync(() -> {
            try (Connection conn = storage.conn()) {
                DSLContext context = DSL.using(conn, storage.dialect());
                return function.apply(context);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> execute(Consumer<DSLContext> function) {
        return runAsync(() -> {
            try (Connection conn = storage.conn()) {
                DSLContext context = DSL.using(conn, storage.dialect());
                function.accept(context);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> CompletableFuture<T> runAsync(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                future.complete(callable.call());
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
