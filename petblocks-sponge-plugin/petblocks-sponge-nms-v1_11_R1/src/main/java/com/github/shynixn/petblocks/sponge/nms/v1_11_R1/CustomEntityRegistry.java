package com.github.shynixn.petblocks.sponge.nms.v1_11_R1;

import com.github.shynixn.petblocks.sponge.nms.CustomEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class CustomEntityRegistry implements CustomEntityType.WrappedRegistry {

    private final Set<Class<?>> registeredClasses = new HashSet<>();

    /**
     * Registers a new customEntityClass with the given parameters. Overrides any existing registrations of this customEntityClazz.
     *
     * @param customEntityClazz customEntityClass
     * @param customEntityType  type
     * @throws Exception exception
     */
    @Override
    public void register(Class<?> customEntityClazz, CustomEntityType customEntityType) throws Exception {
        final ResourceLocation minecraftKey = new ResourceLocation("PetBlocks", customEntityType.getSaveGame_11());
        final RegistryNamespaced<ResourceLocation, Class<? extends Entity>> materialRegistry = EntityList.REGISTRY;
        materialRegistry.register(customEntityType.getEntityId(), minecraftKey, (Class<? extends Entity>) customEntityClazz);
        System.out.println("ADDED");
        this.registeredClasses.add(customEntityClazz);
    }

    /**
     * Unregisters the customEntityClass with the given parameters. Throws an exception if already unregistered.
     *
     * @param customEntityClazz customEntityClass
     * @param customEntityType  type
     * @throws Exception exception
     */
    @Override
    public void unregister(Class<?> customEntityClazz, CustomEntityType customEntityType) throws Exception {
        if (!this.isRegistered(customEntityClazz)) {
            throw new IllegalArgumentException("Entity is already unregisterd!");
        }
        final ResourceLocation minecraftKey = new ResourceLocation("PetBlocks", customEntityType.getSaveGame_11());
        final RegistryNamespaced<ResourceLocation, Class<? extends Entity>> materialRegistry = EntityList.REGISTRY;
        materialRegistry.registryObjects.remove(minecraftKey);
        this.registeredClasses.remove(customEntityClazz);
    }

    /**
     * Returns if the customEntityClazz is still registered.
     *
     * @param customEntityClazz customEntityClass
     * @return isRegistered
     */
    @Override
    public boolean isRegistered(Class<?> customEntityClazz) {
        System.out.println("REGISTERD?????");
        return this.registeredClasses.contains(customEntityClazz);
    }
}
