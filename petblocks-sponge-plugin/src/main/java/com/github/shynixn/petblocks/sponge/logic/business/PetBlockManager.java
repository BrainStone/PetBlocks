package com.github.shynixn.petblocks.sponge.logic.business;

import com.github.shynixn.petblocks.api.persistence.controller.PetMetaController;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.sponge.entity.SpongePetBlock;
import com.github.shynixn.petblocks.core.logic.business.PetRunnable;
import com.github.shynixn.petblocks.core.logic.business.entity.GuiPageContainer;
import com.github.shynixn.petblocks.core.logic.business.helper.ExtensionHikariConnectionContext;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.commandexecutor.PetBlockReloadCommandExecutor;
import com.github.shynixn.petblocks.sponge.logic.business.commandexecutor.PetDataCommandExecutor;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.controller.PetBlockRepository;
import com.github.shynixn.petblocks.sponge.logic.business.listener.SpongePetBlockListener;
import com.github.shynixn.petblocks.sponge.logic.business.listener.SpongePetDataListener;
import com.github.shynixn.petblocks.sponge.logic.persistence.controller.SpongePetDataRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@Singleton
public class PetBlockManager {
    public final Set<Player> carryingPet = new HashSet<>();
    public final Map<Player, Integer> timeBlocked = new HashMap<>();
    public final Map<Player, Inventory> inventories = new HashMap<>();
    public final Map<Player, GuiPageContainer> pages = new HashMap<>();

    @Inject
    private PluginContainer plugin;

    @Inject
    private PetBlockRepository petBlockController;

    @Inject
    private SpongePetDataRepository petDataRepository;

    @Inject
    public SpongeGUI gui;

    @Inject
    private PetDataCommandExecutor petDataCommandExecutor;

    @Inject
    private PetBlockReloadCommandExecutor reloadCommandExecutor;

    @Inject
    private SpongePetBlockListener petBlockListener;

    @Inject
    private SpongePetDataListener petDataListener;

    private final ExtensionHikariConnectionContext connectionContext;

    @Inject
    public PetBlockManager(PluginContainer plugin, Config config) {
        config.reload();
        this.connectionContext = initialize(plugin, config, false);
    }

    public PetMetaController getPetMetaController() {
        return this.petDataRepository;
    }

    public PetBlockRepository getPetBlockController() {
        return this.petBlockController;
    }

    @Deprecated
    public ExtensionHikariConnectionContext getConnectionContext() {
        return this.connectionContext;
    }

    public void providePetblockData(Player player, PetRunnable<SpongePetBlock> runnable) {
        final Optional<SpongePetBlock> petBlock;
        if ((petBlock = this.getPetBlockController().getFromPlayer(player)).isPresent()) {
            runnable.run(petBlock.get().getMeta(), petBlock.get());
        } else {
            Task.builder().async().execute(() -> {
                if (!this.getPetMetaController().hasEntry(player))
                    return;
                final PetMeta petMeta = this.getPetMetaController().getByPlayer(player);
                Task.builder().execute(() -> runnable.run(petMeta, null)).submit(this.plugin);
            }).submit(this.plugin);
        }
    }

    private static synchronized ExtensionHikariConnectionContext initialize(PluginContainer plugin, Config config, boolean modifier) {
        ExtensionHikariConnectionContext connectionContext = null;
        ExtensionHikariConnectionContext.SQlRetriever retriever = fileName -> {
            try {
                final Asset asset = Sponge.getAssetManager().getAsset(plugin, "sql/" + fileName + ".sql").get();
                return asset.readString();
            } catch (final IOException e) {
                PetBlocksPlugin.logger().warn("Cannot read file.", fileName);
                throw new RuntimeException(e);
            }
        };
        if (!((boolean) config.getData("sql.enabled")) || modifier) {
            try {
                final Path file = config.getPrivateConfigDir().resolve("PetBlocks.db");
                if (!Files.exists(file)) {
                    Files.createFile(file);
                }
                connectionContext = ExtensionHikariConnectionContext.from(ExtensionHikariConnectionContext.SQLITE_DRIVER, "jdbc:sqlite:" + file.toFile().getAbsolutePath(), retriever);
                try (Connection connection = connectionContext.getConnection()) {
                    connectionContext.execute("PRAGMA foreign_keys=ON", connection);
                }
            } catch (final SQLException e) {
                PetBlocksPlugin.logger().warn("Cannot execute statement.", e);
            } catch (final IOException e) {
                PetBlocksPlugin.logger().warn("Cannot read file.", e);
            }
            try (Connection connection = connectionContext.getConnection()) {
                for (final String data : connectionContext.getStringFromFile("create-sqlite").split(Pattern.quote(";"))) {
                    connectionContext.executeUpdate(data, connection);
                }
            } catch (final Exception e) {
                PetBlocksPlugin.logger().warn("Cannot execute creation.", e);
            }
        } else {
            try {
                connectionContext = ExtensionHikariConnectionContext.from(ExtensionHikariConnectionContext.MYSQL_DRIVER, "jdbc:mysql://"
                        , config.getData("sql.host")
                        , config.getData("sql.port")
                        , config.getData("sql.database")
                        , config.getData("sql.username")
                        , config.getData("sql.password")
                        , retriever);
            } catch (final IOException e) {
                PetBlocksPlugin.logger().warn("Cannot connect to MySQL database!", e);
                PetBlocksPlugin.logger().warn("Trying to connect to SQLite database....", e);
                return initialize(plugin, config, true);
            }

            boolean oldData = false;
            try (Connection connection = connectionContext.getConnection()) {
                final ResultSet set = connectionContext.executeQuery("SELECT * FROM shy_petblock", connection).executeQuery();
                boolean foundEngineColumn = false;
                for (int i = 1; i <= set.getMetaData().getColumnCount(); i++) {
                    final String name = set.getMetaData().getColumnName(i);
                    if (name.equals("movement_type")) {
                        oldData = true;
                    }
                    if (name.equals("engine")) {
                        foundEngineColumn = true;
                    }
                }
                if (!foundEngineColumn) {
                    oldData = true;
                }
            } catch (final SQLException ignored) {

            }
            if (oldData) {
                PetBlocksPlugin.logger().warn("Found old table data. Deleting previous entries...");
                try (Connection connection = connectionContext.getConnection()) {
                    connectionContext.executeUpdate("DROP TABLE shy_petblock", connection);
                    connectionContext.executeUpdate("DROP TABLE shy_particle_effect", connection);
                    connectionContext.executeUpdate("DROP TABLE shy_player", connection);
                    PetBlocksPlugin.logger().warn("Finished deleting data.");
                } catch (final SQLException e) {
                    PetBlocksPlugin.logger().warn( "Failed removing old data.", e);
                }
            }
            try (Connection connection = connectionContext.getConnection()) {
                for (final String data : connectionContext.getStringFromFile("create-mysql").split(Pattern.quote(";"))) {
                    connectionContext.executeUpdate(data, connection);
                }
            } catch (final Exception e) {
                PetBlocksPlugin.logger().warn("Cannot execute creation.", e);
                PetBlocksPlugin.logger().warn("Trying to connect to SQLite database....", e);
                return initialize(plugin, config, false);
            }
        }
        return connectionContext;
    }
}
