package com.github.shynixn.petblocks.sponge.logic.business.listener;

import com.flowpowered.math.vector.Vector3d;
import com.github.shynixn.petblocks.api.PetBlocksApi;
import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.business.enumeration.GUIPage;
import com.github.shynixn.petblocks.api.business.enumeration.Permission;
import com.github.shynixn.petblocks.api.persistence.entity.EngineContainer;
import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.sponge.entity.SpongePetBlock;
import com.github.shynixn.petblocks.sponge.logic.business.PetBlockManager;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.helper.CompatibilityItemType;
import com.github.shynixn.petblocks.sponge.logic.business.helper.SpongePetBlockModifyHelper;
import com.google.inject.Inject;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Listens to events for configuring petblocks and UI.
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
public class SpongePetDataListener extends SimpleSpongeListener {
    private final PetBlockManager manager;
    private final Set<Player> namingPlayers = new HashSet<>();
    private final Set<Player> namingSkull = new HashSet<>();
    private final Set<Player> spamProtection = new HashSet<>();

    @Inject
    private Config config;

    /**
     * Initializes a new PetDataListener
     *
     * @param manager manager
     * @param plugin  plugin
     */
    @Inject
    public SpongePetDataListener(PetBlockManager manager, PluginContainer plugin) {
        super(plugin);
        this.manager = manager;
    }

    /**
     * Removes the petblock from the player when he leaves the server
     *
     * @param event playerQuitEvent
     */
    @Listener
    public void playerQuitEvent(ClientConnectionEvent.Disconnect event) {
        final Player player = event.getTargetEntity();
        if (this.spamProtection.contains(player)) {
            this.spamProtection.remove(player);
        }
        PetBlocksApi.getDefaultPetBlockController().removeByPlayer(player);
    }

    @Listener
    public void playerClickEvent(final ClickInventoryEvent event, @First(typeFilter = Player.class) Player player) {
        if (event.getTargetInventory().getName().get().equals(Config.getInstance().getGUITitle().toPlainSingle())
                && this.manager.inventories.containsKey(player)) {
            event.setCancelled(true);
            final Optional<SpongePetBlock> optPetblock;

            final ItemStack itemStack = event.getTransactions().get(0).getOriginal().createStack();
            final int newSlot = event.getTransactions().get(0).getSlot().getProperties(SlotIndex.class).toArray(new SlotIndex[0])[0].getValue();
            if ((optPetblock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
                this.handleClick(itemStack, newSlot, player, optPetblock.get().getMeta(), optPetblock.get());
            } else {
                Task.builder().async().execute(() -> {
                    final Optional<PetMeta> optPetMeta = SpongePetDataListener.this.manager.getPetMetaController().getFromPlayer(player);
                    Task.builder().execute(() -> optPetMeta.ifPresent(petMeta -> SpongePetDataListener.this.handleClick(itemStack, newSlot, player, petMeta, null))).submit(SpongePetDataListener.this.plugin);
                }).submit(this.plugin);
            }
        }
    }

    /**
     * Gets called when a player joins a server. Overrides existing pets if enabled in the config.yml and
     * spawns the petblock of the player when his pet was enabled when he left the server the last time.
     *
     * @param event event
     */
    @Listener
    public void playerJoinEvent(final ClientConnectionEvent.Join event) {
        final Player player = event.getTargetEntity();
        Task.builder().async().execute(() -> {
            final PetMeta petMeta;
            if (SpongePetDataListener.this.config.isJoin_enabled()) {
                if (!SpongePetDataListener.this.manager.getPetMetaController().getFromPlayer(player).isPresent() || Config.getInstance().isJoin_overwriteExistingPet()) {
                    if (player.getWorld() != null) {
                        final PetMeta meta = SpongePetDataListener.this.manager.getPetMetaController().create(player);
                        SpongePetDataListener.this.config.fixJoinDefaultPet(meta);
                        this.manager.getPetMetaController().store(meta);
                    }
                }
            }
            if ((petMeta = PetBlocksApi.getDefaultPetMetaController().getByPlayer(player)) != null) {
                if (petMeta.isEnabled()) {
                    Task.builder().execute(() -> {
                        if (player.getWorld() != null) {
                            final PetBlock petBlock = PetBlocksApi.getDefaultPetBlockController().create(player, petMeta);
                            PetBlocksApi.getDefaultPetBlockController().store(petBlock);
                        }
                    }).delayTicks(2L).submit(this.plugin);
                }
            }
        }).submit(this.plugin);
    }

    @Listener
    public void inventoryCloseEvent(ClickInventoryEvent.Close event, @First(typeFilter = Player.class) Player player) {
        System.out.println("CLOSE EVENTORY");
        if (this.manager.inventories.containsKey(player)) {
            this.manager.inventories.remove(player);
        }
    }

    @Listener
    public void playerChatEvent(MessageChannelEvent.Chat event, @First(typeFilter = Player.class) Player player) {
        if (!Config.getInstance().isChat_async() && Config.getInstance().isChatHighestPriority()) {
            if (this.namingPlayers.contains(player) || this.namingSkull.contains(player)) {
                event.setCancelled(true);
            }
            final PetBlock petBlock;
            if ((petBlock = this.getPetBlock(player)) != null) {
                if (this.namingSkull.contains(player)) {
                    this.renameSkull(player, event.getMessage(), petBlock.getMeta(), petBlock);
                } else if (this.namingPlayers.contains(player)) {
                    this.renameName(player, event.getMessage(), petBlock.getMeta(), petBlock);
                }
            } else {
                Task.builder().async().execute(() -> {
                    final PetMeta petMeta = this.manager.getPetMetaController().getByPlayer(player);
                    if (this.namingSkull.contains(player)) {
                        Task.builder().execute(() -> this.renameSkull(player, event.getMessage(), petMeta, null)).submit(this.plugin);
                    } else if (this.namingPlayers.contains(player)) {
                        Task.builder().execute(() -> this.renameName(player, event.getMessage(), petMeta, null)).submit(this.plugin);
                    }
                }).submit(this.plugin);
            }
        }
    }

    private void handleClick(ItemStack currentItem, int slot, Player player, PetMeta petMeta, SpongePetBlock petBlock) {
        final int itemSlot = slot + this.manager.pages.get(player).currentCount + 1;
        System.out.println("HANDLE CLICK " + itemSlot + " on " + slot);
        if (this.manager.pages.get(player).page == GUIPage.MAIN && this.getGUIItem("my-pet").getPosition() == slot) {
            this.handleClickOnMyPetItem(player, petMeta);
        } else if (this.isGUIItem(currentItem, "enable-pet")) {
            if (!this.spamProtection.contains(player)) {
                this.setPetBlock(player, petMeta);
                this.refreshGUI(player, petMeta);
            }
            this.handleSpamProtection(player);
        } else if (this.isGUIItem(currentItem, "disable-pet")) {
            if (!this.spamProtection.contains(player)) {
                this.removePetBlock(player, petMeta);
                this.refreshGUI(player, petMeta);
            }
            this.handleSpamProtection(player);
        } else if (this.isGUIItem(currentItem, "sounds-enabled-pet")) {
            petMeta.setSoundEnabled(false);
            this.refreshGUI(player, petMeta);
            this.persistAsynchronously(petMeta);
        } else if (this.isGUIItem(currentItem, "sounds-disabled-pet")) {
            petMeta.setSoundEnabled(true);
            this.refreshGUI(player, petMeta);
            this.persistAsynchronously(petMeta);
        } else if (this.isGUIItem(currentItem, "next-page")) {
            this.manager.gui.moveList(player, true);
        } else if (this.isGUIItem(currentItem, "previous-page")) {
            this.manager.gui.moveList(player, false);
        } else if (this.isGUIItem(currentItem, "ordinary-costume")) {
            this.manager.gui.setPage(player, GUIPage.DEFAULT_COSTUMES, petMeta);
        } else if (this.isGUIItem(currentItem, "color-costume")) {
            this.manager.gui.setPage(player, GUIPage.COLOR_COSTUMES, petMeta);
        } else if (this.isGUIItem(currentItem, "rare-costume")) {
            this.manager.gui.setPage(player, GUIPage.CUSTOM_COSTUMES, petMeta);
        } else if (this.isGUIItem(currentItem, "minecraft-heads-costume")) {
            //  Bukkit.getServer().getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(PetBlocksPlugin.class), () -> this.collectedMinecraftHeads.sendMessage(player));
            this.manager.gui.setPage(player, GUIPage.MINECRAFTHEADS_COSTUMES, petMeta);
        } else if (this.isGUIItem(currentItem, "particle-pet")) {
            this.manager.gui.setPage(player, GUIPage.PARTICLES, petMeta);
        } else if (this.isGUIItem(currentItem, "wardrobe")) {
            this.manager.gui.setPage(player, GUIPage.WARDROBE, petMeta);
        } else if (this.isGUIItem(currentItem, "engine-settings")) {
            this.manager.gui.setPage(player, GUIPage.ENGINES, petMeta);
        } else if (this.isGUIItem(currentItem, "call-pet") && petBlock != null) {
            petBlock.teleport(player.getTransform());
            this.closeInventory(player);
        } else if (this.isGUIItem(currentItem, "hat-pet") && this.hasPermission(player, Permission.WEARPET) && petBlock != null) {
            petBlock.wear(player);
        } else if (this.isGUIItem(currentItem, "riding-pet") && this.hasPermission(player, Permission.RIDEPET) && petBlock != null) {
            petBlock.ride(player);
        } else if (this.isGUIItem(currentItem, "naming-pet") && this.hasPermission(player, Permission.RENAMEPET)) {
            this.namingPlayers.add(player);
            this.closeInventory(player);
            player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getNamingMessage()));
        } else if (this.isGUIItem(currentItem, "skullnaming-pet") && this.hasPermission(player, Permission.RENAMESKULL)) {
            this.namingSkull.add(player);
            this.closeInventory(player);
            player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getSkullNamingMessage()));
        } else if (this.isGUIItem(currentItem, "cannon-pet") && this.hasPermission(player, Permission.CANNON) && petBlock != null) {
            petBlock.setVelocity(this.getDirection(player));
            this.closeInventory(player);
        } else if (this.isGUIItem(currentItem, "back")) {
            this.manager.gui.backPage(player, petMeta);
        } else if (this.manager.pages.get(player).page == GUIPage.ENGINES && this.hasPermission(player, Permission.ALLPETTYPES.get(), Permission.SINGLEPETTYPE.get() + "" + itemSlot)) {
            final EngineContainer engineContainer = Config.getInstance().getEngineController().getById(itemSlot);
            if(engineContainer == null)
                return;
            System.out.println("SELECTED ENGINE: " + engineContainer);
            SpongePetBlockModifyHelper.setEngine(petMeta, petBlock, engineContainer);
            this.persistAsynchronously(petMeta);
            this.manager.gui.setPage(player, GUIPage.MAIN, petMeta);
        } else if (this.manager.pages.get(player).page == GUIPage.PARTICLES && this.hasPermission(player, Permission.ALLPARTICLES.get(), Permission.SINGLEPARTICLE.get() + "" + itemSlot)) {
            final GUIItemContainer container = Config.getInstance().getParticleController().getContainerByPosition(itemSlot);
            if(container == null)
                return;
            SpongePetBlockModifyHelper.setParticleEffect(petMeta, petBlock, container);
            this.persistAsynchronously(petMeta);
            this.manager.gui.setPage(player, GUIPage.MAIN, petMeta);
        } else if (slot < 45 && this.manager.pages.get(player).page == GUIPage.DEFAULT_COSTUMES && this.hasPermission(player, Permission.ALLDEFAULTCOSTUMES.get(), Permission.SINGLEDEFAULTCOSTUME.get() + "" + itemSlot)) {
            final GUIItemContainer container = Config.getInstance().getOrdinaryCostumesController().getContainerByPosition(itemSlot);
            if(container == null)
                return;
            this.setCostumeSkin(player, petMeta, petBlock, container);
        } else if (slot < 45 && this.manager.pages.get(player).page == GUIPage.COLOR_COSTUMES && this.hasPermission(player, Permission.ALLCOLORCOSTUMES.get(), Permission.SINGLECOLORCOSTUME.get() + "" + itemSlot)) {
            final GUIItemContainer container = Config.getInstance().getColorCostumesController().getContainerByPosition(itemSlot);
            if(container == null)
                return;
            this.setCostumeSkin(player, petMeta, petBlock, container);
        } else if (slot < 45 && this.manager.pages.get(player).page == GUIPage.CUSTOM_COSTUMES && this.hasPermission(player, Permission.ALLCUSTOMCOSTUMES.get(), Permission.SINGLECUSTOMCOSTUME.get() + "" + itemSlot)) {
            final GUIItemContainer container = Config.getInstance().getRareCostumesController().getContainerByPosition(itemSlot);
            if(container == null)
                return;
            this.setCostumeSkin(player, petMeta, petBlock, container);
        } else if (slot < 45 && this.manager.pages.get(player).page == GUIPage.MINECRAFTHEADS_COSTUMES && this.hasPermission(player, Permission.ALLHEADATABASECOSTUMES.get(), Permission.SINGLEMINECRAFTHEADSCOSTUME.get() + "" + itemSlot)) {
            final GUIItemContainer container = Config.getInstance().getMinecraftHeadsCostumesController().getContainerByPosition(itemSlot);
            if(container == null)
                return;
           this.setCostumeSkin(player, petMeta, petBlock, container);
        }
    }

    /**
     * Sets the skin for the given petMeta and petblock of the gui container
     *
     * @param petMeta   petMeta
     * @param petBlock  petBlock
     * @param container container
     */
    private void setCostumeSkin(Player player, PetMeta petMeta, PetBlock petBlock, GUIItemContainer container) {
        SpongePetBlockModifyHelper.setCostume(petMeta, petBlock, container);
        this.persistAsynchronously(petMeta);
        this.manager.gui.setPage(player, GUIPage.MAIN, petMeta);
    }

    /**
     * Gets called when the player clicks on the my-pet icon.
     * If Only disable Item is enabled, the petblock spawns otherwise the petblock meta gets reset
     *
     * @param player  player
     * @param petMeta petMeta
     */
    private void handleClickOnMyPetItem(Player player, PetMeta petMeta) {
        final PetBlock petBlock;
        if ((petBlock = this.getPetBlock(player)) == null && Config.getInstance().isOnlyDisableItemEnabled()) {
            this.setPetBlock(player, petMeta);
            this.manager.gui.setPage(player, GUIPage.MAIN, petMeta);
        } else {
            if (Config.getInstance().isCopySkinEnabled()) {
                petMeta.setDisplaySkin(CompatibilityItemType.SKULL_ITEM.getItemType(), 3, this.getGUIItem("my-pet").getSkin(), this.getGUIItem("my-pet").isItemUnbreakable());
            } else {
                final GUIItemContainer c = this.getGUIItem("default-appearance");
                petMeta.setSkin(c.getItemId(), c.getItemDamage(), c.getSkin(), c.isItemUnbreakable());
            }
            petMeta.getParticleEffectMeta().setEffectType(ParticleEffectMeta.ParticleEffectType.NONE);
            if (petBlock != null) {
                petBlock.respawn();
            }
            this.persistAsynchronously(petMeta);
        }
    }

    private void renameName(Player player, Text message, PetMeta petMeta, PetBlock petBlock) {
        if (message.toPlain().length() > Config.getInstance().pet().getDesign_maxPetNameLength()) {
            player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getNamingErrorMessage()));
        } else {
            try {
                this.namingPlayers.remove(player);
                petMeta.setPetDisplayName(message);
                this.persistAsynchronously(petMeta);
                if (petBlock != null)
                    petBlock.respawn();
                player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getNamingSuccessMessage()));
            } catch (final Exception e) {
                player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getNamingErrorMessage()));
            }
        }
    }

    private void renameSkull(Player player, Text message, PetMeta petMeta, PetBlock petBlock) {
        if (message.toPlain().length() > 20) {
            player.sendMessage(Text.of((Config.getInstance().getPrefix() + Config.getInstance().getSkullNamingErrorMessage())));
        } else {
            try {
                this.namingSkull.remove(player);
                petMeta.setDisplaySkin(CompatibilityItemType.SKULL_ITEM.getItemType(), 3, message.toPlainSingle(), false);
                this.persistAsynchronously(petMeta);
                if (petBlock != null)
                    petBlock.respawn();
                player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getSkullNamingSuccessMessage()));
            } catch (final Exception e) {
                player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getSkullNamingErrorMessage()));
            }
        }
    }

    private void closeInventory(Player player) {
        System.out.println("CLOSING!!!!");
        if (this.manager.inventories.containsKey(player)) {
            this.manager.inventories.remove(player);
        }
        player.closeInventory(Cause.of(NamedCause.owner(this.plugin)));
    }

    /**
     * Handles spamming protection.
     *
     * @param player player
     */
    private void handleSpamProtection(Player player) {
        if (!this.spamProtection.contains(player)) {
            this.spamProtection.add(player);
            Task.builder().execute(() -> this.spamProtection.remove(player)).delayTicks(30L).submit(this.plugin);
        }
    }

    /**
     * Returns the launch Direction for the cannon
     *
     * @param player player
     * @return launchDirection
     */
    private Vector3d getDirection(Player player) {
        final double rotX = player.getHeadRotation().getY();
        final double rotY = player.getHeadRotation().getX();
        final double h = Math.cos(Math.toRadians(rotY));
        return new Vector3d(-h * Math.sin(Math.toRadians(rotX)), 0.5, h * Math.cos(Math.toRadians(rotX)))
                .mul(3);
    }

    /**
     * Refreshes the current GUI page
     *
     * @param player  player
     * @param petMeta petMeta
     */
    private void refreshGUI(Player player, PetMeta petMeta) {
        this.manager.gui.setPage(player, this.manager.pages.get(player).page, petMeta);
    }

    /**
     * Sets the petblock for the given player
     *
     * @param player  player
     * @param petMeta petMeta
     */
    private void setPetBlock(Player player, PetMeta petMeta) {
        petMeta.setEnabled(true);
        final PetBlock petBlock = PetBlocksApi.getDefaultPetBlockController().create(player, petMeta);
        PetBlocksApi.getDefaultPetBlockController().store(petBlock);
        this.persistAsynchronously(petMeta);
    }

    /**
     * Removes the petblock from the given player
     *
     * @param player  player
     * @param petMeta petMeta
     */
    private void removePetBlock(Player player, PetMeta petMeta) {
        petMeta.setEnabled(false);
        PetBlocksApi.getDefaultPetBlockController().removeByPlayer(player);
        this.persistAsynchronously(petMeta);
    }

    private boolean hasPermission(Player player, Permission permission) {
        if (!player.hasPermission(permission.get())) {
            player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getNoPermission()));
            return false;
        }
        return true;
    }

    private boolean hasPermission(Player player, String... permissions) {
        for (final String permission : permissions) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }
        player.sendMessage(Text.of(Config.getInstance().getPrefix() + Config.getInstance().getNoPermission()));
        return false;
    }

    private GUIItemContainer getGUIItem(String name) {
        return this.config.getGuiItemsController().getGUIItemByName(name);
    }

    /**
     * Returns the petblock from the given player
     *
     * @param player player
     * @return petBlock
     */
    private PetBlock getPetBlock(Player player) {
        return PetBlocksApi.getDefaultPetBlockController().getByPlayer(player);
    }

    /**
     * Returns if the given itemStack is the gui Item with the given name
     *
     * @param itemStack itemStack
     * @param name      name
     * @return item
     */
    private boolean isGUIItem(ItemStack itemStack, String name) {
        return this.config.getGuiItemsController().isGUIItem(itemStack, name);
    }

    /**
     * Persists the current petMeta asynchronly
     *
     * @param petMeta petMeta
     */
    private void persistAsynchronously(PetMeta petMeta) {
        Task.builder().async().execute(() -> this.manager.getPetMetaController().store(petMeta)).submit(this.plugin);
    }
}
