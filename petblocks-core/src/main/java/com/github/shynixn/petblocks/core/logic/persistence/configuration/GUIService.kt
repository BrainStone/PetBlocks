package com.github.shynixn.petblocks.core.logic.persistence.configuration

import com.github.shynixn.petblocks.api.persistence.entity.GUIItem
import com.github.shynixn.petblocks.api.persistence.entity.GUIScriptableItem
import com.github.shynixn.petblocks.core.logic.business.helper.ChatColor
import com.github.shynixn.petblocks.core.logic.persistence.entity.GUIItemContainer
import org.slf4j.Logger
import java.util.*
import kotlin.collections.HashMap

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
class GUIService(private val clazz: Class<*>, private val logger: Logger) {
    private val items: MutableMap<String, GUIScriptableItem> = HashMap()
    private val scriptCollections: MutableMap<String, GUIItemCollection<GUIItem>> = HashMap()

    /**
     * Reloads the gui items into the cache.
     */
    fun reload() {
        val data = Config.getInstance<Any>().getData<Map<String, Any>>("gui.items")

        for (key in data.keys) {
            try {
                val container = clazz.getConstructor(Map::class.java).newInstance(data[key]) as GUIItemContainer<*>
                if (key == "suggest-heads") {
                    container.setDisplayName(ChatColor.AQUA.toString() + "" + ChatColor.BOLD + "Suggest Heads")
                }

                this.items[key] = container
            } catch (e: Exception) {
                logger.warn("Failed to load guiItem $key.", e)
            }
        }
    }

    /**
     * Executes the script of the given [guiItem].
     */
    fun <T> executeScript(guiItem: GUIScriptableItem): Optional<T> {
        val script = guiItem.executeScript

        try {
            if (script.startsWith("binding collection ")) {
                val key = script.replace("binding collection ", "")

                if (!scriptCollections.containsKey(key)) {

                    val data = Config.getInstance<Any>().getData<Map<String, Any>>(key)
                    scriptCollections[key] = parseCollection(data)
                }

                @Suppress("UNCHECKED_CAST")
                return Optional.of(scriptCollections[key]!!) as Optional<T>

            } else {
                logger.warn("Cannot find script '$script'")
            }
        } catch (e: Exception) {
            logger.warn("Cannotparse script '$script'", e)
        }

        return Optional.empty()
    }

    /**
     * Returns an [GUIItem] when the given [key] matches an item.
     */
    fun getGUIItem(key: String): Optional<GUIScriptableItem> {
        if (items.containsKey(key)) {
            return Optional.of(items[key]!!)
        }

        return Optional.empty()
    }

    /**
     * Returns an [GUIItem] when the given properties [displayName] and [lore] match an item.
     */
    fun getGUIItemFromItemstackProperties(displayName: String, lore: List<String>): Optional<GUIScriptableItem> {
        items.values.forEach { i ->
            if (i.displayName.equals(displayName, true)) {
                val itemLore = i.lore
                if (itemLore.size == lore.size) {
                    return if ((0..itemLore.size).any { itemLore[it] != lore[it] }) Optional.empty() else Optional.of(i)
                }
            }
        }

        return Optional.empty()
    }

    private fun parseCollection(data: Map<String, Any>): GUIItemCollection<GUIItem> {
        val dataMap = HashMap<Int, GUIItem>()

        data.keys.forEach { key ->
            val container = clazz.getConstructor(Map::class.java).newInstance(data[key]) as GUIItemContainer<*>
            dataMap[key.toInt()] = container
        }

        return GUIItemCollection(dataMap)
    }
}