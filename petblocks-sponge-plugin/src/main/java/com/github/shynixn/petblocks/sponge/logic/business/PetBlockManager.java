package com.github.shynixn.petblocks.sponge.logic.business;

import com.github.shynixn.petblocks.api.business.controller.PetBlockController;
import com.github.shynixn.petblocks.api.persistence.controller.PetMetaController;
import com.github.shynixn.petblocks.core.logic.business.entity.GuiPageContainer;
import com.github.shynixn.petblocks.core.logic.business.helper.ExtensionHikariConnectionContext;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.controller.PetBlockRepository;
import com.github.shynixn.petblocks.sponge.logic.persistence.controller.SpongePetDataRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
public class PetBlockManager implements AutoCloseable {

    public final Set<Player> carryingPet = new HashSet<>();
    public final Map<Player, Integer> timeBlocked = new HashMap<>();
    public final Set<Player> headDatabasePlayers = new HashSet<>();
    public final Map<Player, Inventory> inventories = new HashMap<>();
    public final Map<Player, GuiPageContainer> pages = new HashMap<>();
    //public GUI gui;

    @Inject
    private PluginContainer plugin;

    @Inject
    private PetBlockRepository petBlockController;

    @Inject
    private SpongePetDataRepository petDataRepository;

    @Inject
    public SpongeGUI gui;

    private final ExtensionHikariConnectionContext connectionContext;

    @Inject
    public PetBlockManager(PluginContainer plugin, Config config) {
        this.connectionContext = initialize(plugin, config, false);
    }

    /*  @Inject
    private PetBlock petMetaController;

    public PetBlockManager() {
        super();
        this.petBlockController = Factory.createPetBlockController();
        this.petMetaController = Factory.createPetDataController();
        try {
            new PetDataCommandExecutor(this);
            new PetBlockCommandExecutor(this);
            new PetBlockReloadCommandExecutor(plugin);
            new PetDataListener(this, plugin);
            new PetBlockListener(this, plugin);
            this.filter = PetBlockFilter.create();
            this.gui = new GUI(this);
        } catch (final Exception e) {
            PetBlocksPlugin.logger().log(Level.WARNING, "Failed to initialize petblockmanager.", e);
        }
    }
    public PetBlockController getPetBlockController() {
        return this.petBlockController;
    }*/

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

    private static synchronized ExtensionHikariConnectionContext initialize(PluginContainer plugin, Config config, boolean modifier) {
        ExtensionHikariConnectionContext connectionContext = null;
        ExtensionHikariConnectionContext.SQlRetriever retriever = fileName -> {
            try {
                final Asset asset = Sponge.getAssetManager().getAsset(plugin, "sql/" + fileName + ".sql").get();
                return asset.readString();
            } catch (final IOException e) {
                PetBlocksPlugin.logger().log(Level.WARNING, "Cannot read file.", fileName);
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
                PetBlocksPlugin.logger().log(Level.WARNING, "Cannot execute statement.", e);
            } catch (final IOException e) {
                PetBlocksPlugin.logger().log(Level.WARNING, "Cannot read file.", e);
            }
            try (Connection connection = connectionContext.getConnection()) {
                for (final String data : connectionContext.getStringFromFile("create-sqlite").split(Pattern.quote(";"))) {
                    connectionContext.executeUpdate(data, connection);
                }
            } catch (final Exception e) {
                PetBlocksPlugin.logger().log(Level.WARNING, "Cannot execute creation.", e);
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
                PetBlocksPlugin.logger().log(Level.WARNING, "Cannot connect to MySQL database!", e);
                PetBlocksPlugin.logger().log(Level.WARNING, "Trying to connect to SQLite database....", e);
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
                PetBlocksPlugin.logger().log(Level.WARNING, "Found old table data. Deleting previous entries...");
                try (Connection connection = connectionContext.getConnection()) {
                    connectionContext.executeUpdate("DROP TABLE shy_petblock", connection);
                    connectionContext.executeUpdate("DROP TABLE shy_particle_effect", connection);
                    connectionContext.executeUpdate("DROP TABLE shy_player", connection);
                    PetBlocksPlugin.logger().log(Level.WARNING, "Finished deleting data.");
                } catch (final SQLException e) {
                    PetBlocksPlugin.logger().log(Level.WARNING, "Failed removing old data.", e);
                }
            }
            try (Connection connection = connectionContext.getConnection()) {
                for (final String data : connectionContext.getStringFromFile("create-mysql").split(Pattern.quote(";"))) {
                    connectionContext.executeUpdate(data, connection);
                }
            } catch (final Exception e) {
                PetBlocksPlugin.logger().log(Level.WARNING, "Cannot execute creation.", e);
                PetBlocksPlugin.logger().log(Level.WARNING, "Trying to connect to SQLite database....", e);
                return initialize(plugin, config, false);
            }
        }
        return connectionContext;
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
      /*  for (final Player player : this.carryingPet) {
            NMSRegistry.setItemInHand19(player, null, true);
        }
        this.timeBlocked.clear();
        this.headDatabasePlayers.clear();
        this.inventories.clear();
        this.pages.clear();
        this.petBlockController.close();
        this.petMetaController.close();
        this.filter.close();
        this.carryingPet.clear();*/
    }
}
