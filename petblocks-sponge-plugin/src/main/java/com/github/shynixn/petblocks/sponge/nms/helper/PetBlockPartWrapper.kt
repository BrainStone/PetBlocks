package com.github.shynixn.petblocks.sponge.nms.helper

import com.github.shynixn.petblocks.api.business.entity.PetBlockPartEntity
import com.github.shynixn.petblocks.sponge.nms.VersionSupport
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.Transform
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.monster.Zombie

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
class PetBlockPartWrapper(private val engine: Living) : PetBlockPartEntity<Entity> {

    object Companion {
        var spawnMethodRabbit = Class.forName("com.github.shynixn.petblocks.sponge.nms.VERSION.CustomRabbit".replace("VERSION", VersionSupport.getServerVersion().versionText)).getDeclaredMethod("spawn", Transform::class.java)
        var spawnMethodZombie = Class.forName("com.github.shynixn.petblocks.sponge.nms.VERSION.CustomZombie".replace("VERSION", VersionSupport.getServerVersion().versionText)).getDeclaredMethod("spawn", Transform::class.java)
    }

    /**
     * Returns the entity hidden by this object.
     *
     * @return spigotEntity
     */
    override fun getEntity(): Entity {
        return engine
    }

    /**
     * Spawns the entity at the given location.
     *
     * @param location location
     */
    override fun spawn(location: Any?) {
        if (engine is Zombie) {
            Companion.spawnMethodZombie.invoke(engine, location)
        } else {
            Companion.spawnMethodRabbit.invoke(engine, location)
        }
    }

    /**
     * Removes the entity from the world.
     */
    override fun remove() {
        engine.remove()
    }
}