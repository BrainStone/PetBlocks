package com.github.shynixn.petblocks.sponge.logic.business.helper;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
public class UpdateUtils {

    private static final String BASE_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

    /**
     * Checks if the given plugin version and spigot resource Id is the same by doing an webRequest
     *
     * @param resourceId spigot resourceId
     * @param plugin     plugin
     * @return isUpToDate
     * @throws IOException exception
     */
    public static boolean isPluginUpToDate(long resourceId, PluginContainer plugin) throws IOException {
        return plugin.getVersion().get().equals(getLatestReleaseVersion(resourceId));
    }

    /**
     * Checks if the given plugin version and spigot resource Id is the same by doing and webRequest and printing the result into the console
     *
     * @param resourceId spigot resourceId
     * @param prefix     prefix
     * @param pluginName pluginName
     * @param plugin     plugin
     * @throws IOException exception
     */
    public static void checkPluginUpToDateAndPrintMessage(long resourceId, String prefix, String pluginName, PluginContainer plugin) throws IOException {
        if (!isPluginUpToDate(resourceId, plugin)) {
            if (plugin.getVersion().get().endsWith("SNAPSHOT")) {
                sendServerMessage(prefix + "&e================================================");
                sendServerMessage(prefix + "&eYou are using a snapshot of " + pluginName);
                sendServerMessage(prefix + "&eCheck regularly if there is a new version available");
                sendServerMessage(prefix + "&e================================================");
            } else {
                sendServerMessage(prefix + "&e================================================");
                sendServerMessage(prefix + "&e" + pluginName + " is outdated");
                sendServerMessage(prefix + "&e" + "Please download the latest version from spigotmc.org");
                sendServerMessage(prefix + "&e" + "================================================");
            }
        }
    }

    private static void sendServerMessage(String text) {
        Sponge.getServer().getConsole().sendMessage(SpongePetBlockModifyHelper.translateStringToText(text));
    }

    /**
     * Returns the latest version by a webRequest
     *
     * @param resourceId resource
     * @return version
     * @throws IOException exception
     */
    private static String getLatestReleaseVersion(long resourceId) throws IOException {
        final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(BASE_URL + resourceId).openConnection();
        try (InputStream stream = httpsURLConnection.getInputStream(); InputStreamReader reader = new InputStreamReader(stream); BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.readLine();
        }
    }
}
