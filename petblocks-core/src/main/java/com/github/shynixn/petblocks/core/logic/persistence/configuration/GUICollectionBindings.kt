package com.github.shynixn.petblocks.core.logic.persistence.configuration

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer
import com.github.shynixn.petblocks.core.logic.business.scripting.Scriptable
import com.google.inject.Inject
import org.slf4j.Logger
import java.util.*

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
abstract class GUICollectionBindings @Inject constructor(private val logger : Logger) : Scriptable {
    private val bindings = HashMap<String, GUIItemCollection>()

    /**
     * Reloads all bindings from the config.yml.
     */
    fun reload()
    {
        bindings.clear()
        val collections = Config.getInstance<Config<*>>().getData<Map<String, Any>>("collections")

        collections.keys.forEach { key ->

            val items = collections[key] as Map<String, Any>

            items.forEach { numberKey ->

                val contentMap = items[numberKey] as Map<String, Any>

                if(contentMap.containsKey("effect"))
                {

                }


            }



        }
    }

    /**
     * Executes the given executing line.
     */
    override fun <T> execute(execution: String): Optional<T> {
        try {
            if (!execution.startsWith("binding collection"))
                return Optional.empty()

            val key = execution.replace("binding collection ", "")

            if (!bindings.containsKey(key)) {
                logger.warn("Failed to retrieve binding collection '$key'.")
                return Optional.empty()
            }

            @Suppress("UNCHECKED_CAST")
            return Optional.of(bindings[key] as T);
        } catch (e: Exception) {
            logger.error("Failed to parse execution $execution.", e)
            return Optional.empty()
        }
    }

    private fun parseParticleEffectCollection(bindingName : String, key : Int, content : Map<String, Any>)
    {



    }
}