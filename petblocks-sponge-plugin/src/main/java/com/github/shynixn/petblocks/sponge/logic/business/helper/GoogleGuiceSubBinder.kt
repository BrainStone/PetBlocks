package com.github.shynixn.petblocks.sponge.logic.business.helper

import com.github.shynixn.petblocks.core.logic.business.helper.ExtensionHikariConnectionContext
import com.github.shynixn.petblocks.sponge.logic.business.PetBlocksManager
import com.github.shynixn.petblocks.sponge.logic.persistence.configuration.Config
import com.google.inject.AbstractModule
import com.google.inject.Inject
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.plugin.PluginContainer
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.sql.SQLException
import java.util.regex.Pattern

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
class GoogleGuiceSubBinder : AbstractModule() {

    @Inject
    @ConfigDir(sharedRoot = false)
    private lateinit var privateConfigDir: Path

    @Inject
    private lateinit var logger: Logger

    @Inject
    private lateinit var plugin : PluginContainer

    var petBlocksManager : PetBlocksManager? = null

    private fun initialize(modifier: Boolean): ExtensionHikariConnectionContext? {
        var connectionContext: ExtensionHikariConnectionContext? = null
        val config = Config
        val retriever = ExtensionHikariConnectionContext.SQlRetriever { fileName ->
            try {
                val asset = Sponge.getAssetManager().getAsset(plugin, "sql/$fileName.sql").get()
                asset.readString()
            } catch (e: IOException) {
                logger.warn("Cannot read file.", fileName)
                throw RuntimeException(e)
            }
        }
        if (!(Config.getData<String>("sql.enabled") as Boolean) || modifier) {
            try {
                val file = privateConfigDir.resolve("PetBlocks.db")
                if (!Files.exists(file)) {
                    Files.createFile(file)
                }
                connectionContext = ExtensionHikariConnectionContext.from(ExtensionHikariConnectionContext.SQLITE_DRIVER, "jdbc:sqlite:" + file.toFile().absolutePath, false, retriever)
                connectionContext!!.connection.use { connection -> connectionContext!!.execute("PRAGMA foreign_keys=ON", connection) }
            } catch (e: SQLException) {
                logger.warn("Cannot execute statement.", e)
            } catch (e: IOException) {
                logger.warn("Cannot read file.", e)
            }

            try {
                connectionContext!!.connection.use { connection ->
                    for (data in connectionContext!!.getStringFromFile("create-sqlite").split(Pattern.quote(";").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                        connectionContext!!.executeUpdate(data, connection)
                    }
                }
            } catch (e: Exception) {
                logger.warn("Cannot execute creation.", e)
            }

        } else {
            try {
                connectionContext = ExtensionHikariConnectionContext.from(ExtensionHikariConnectionContext.MYSQL_DRIVER, "jdbc:mysql://", config.getData<String>("sql.host"), config.getData<Int>("sql.port")!!, config.getData<String>("sql.database"), config.getData<String>("sql.username"), config.getData<String>("sql.password"), Config.getData<Boolean>("useSSL")!!, retriever)
            } catch (e: IOException) {
                logger.warn("Cannot connect to MySQL database!", e)
                logger.warn("Trying to connect to SQLite database....", e)
                return initialize(true)
            }

            var oldData = false
            try {
                connectionContext!!.connection.use { connection ->
                    val set = connectionContext.executeQuery("SELECT * FROM shy_petblock", connection).executeQuery()
                    var foundEngineColumn = false
                    for (i in 1..set.metaData.columnCount) {
                        val name = set.metaData.getColumnName(i)
                        if (name == "movement_type") {
                            oldData = true
                        }
                        if (name == "engine") {
                            foundEngineColumn = true
                        }
                    }
                    if (!foundEngineColumn) {
                        oldData = true
                    }
                }
            } catch (ignored: SQLException) {

            }

            if (oldData) {
                logger.warn("Found old table data. Deleting previous entries...")
                try {
                    connectionContext!!.connection.use { connection ->
                        connectionContext.executeUpdate("DROP TABLE shy_petblock", connection)
                        connectionContext.executeUpdate("DROP TABLE shy_particle_effect", connection)
                        connectionContext.executeUpdate("DROP TABLE shy_player", connection)
                        logger.warn("Finished deleting data.")
                    }
                } catch (e: SQLException) {
                    logger.warn("Failed removing old data.", e)
                }

            }
            try {
                connectionContext!!.connection.use { connection ->
                    for (data in connectionContext.getStringFromFile("create-mysql").split(Pattern.quote(";").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                        connectionContext.executeUpdate(data, connection)
                    }
                }
            } catch (e: Exception) {
                logger.warn("Cannot execute creation.", e)
                logger.warn("Trying to connect to SQLite database....", e)
                return initialize(false)
            }

        }
        return connectionContext
    }

    override fun configure() {
        val connectionContext = initialize(false)
        petBlocksManager = PetBlocksManager()
        bind(ExtensionHikariConnectionContext::class.java).toInstance(connectionContext)
        bind(PetBlocksManager::class.java).toInstance(petBlocksManager)
    }
}