package com.github.shynixn.petblocks.logic.persistence.controller;

import com.github.shynixn.petblocks.bukkit.nms.VersionSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
public class VersionSupportIT {

    @Test
    public void versionSameOrGreaterSupportTest() {
        assertEquals(false, VersionSupport.VERSION_1_8_R1.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_8_R2));
        assertEquals(true, VersionSupport.VERSION_1_9_R2.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R2));
        assertEquals(true, VersionSupport.VERSION_1_9_R2.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_8_R2));
    }

    @Test
    public void versionGreaterSupportTest() {
        assertEquals(false, VersionSupport.VERSION_1_8_R1.isVersionGreaterThan(VersionSupport.VERSION_1_8_R2));
        assertEquals(false, VersionSupport.VERSION_1_12_R1.isVersionGreaterThan(VersionSupport.VERSION_1_12_R1));
        assertEquals(true, VersionSupport.VERSION_1_9_R2.isVersionGreaterThan(VersionSupport.VERSION_1_8_R2));
    }

    @Test
    public void versionSameOrLowerSupportTest() {
        assertEquals(true, VersionSupport.VERSION_1_8_R1.isVersionSameOrLowerThan(VersionSupport.VERSION_1_8_R2));
        assertEquals(true, VersionSupport.VERSION_1_9_R2.isVersionSameOrLowerThan(VersionSupport.VERSION_1_9_R2));
        assertEquals(false, VersionSupport.VERSION_1_9_R2.isVersionSameOrLowerThan(VersionSupport.VERSION_1_8_R2));
    }

    @Test
    public void versionLowerSupportTest() {
        assertEquals(true, VersionSupport.VERSION_1_8_R1.isVersionLowerThan(VersionSupport.VERSION_1_8_R2));
        assertEquals(false, VersionSupport.VERSION_1_9_R2.isVersionLowerThan(VersionSupport.VERSION_1_9_R2));
        assertEquals(false, VersionSupport.VERSION_1_9_R2.isVersionLowerThan(VersionSupport.VERSION_1_8_R2));
    }
}
