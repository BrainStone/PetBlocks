package com.github.shynixn.petblocks.sponge.logic.business.commandexecutor;

import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.business.enumeration.GUIPage;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.sponge.entity.SpongePetBlock;
import com.github.shynixn.petblocks.core.logic.business.PetRunnable;
import com.github.shynixn.petblocks.sponge.logic.business.PetBlockManager;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.helper.CompatibilityItemType;
import com.google.inject.Inject;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;

public final class PetDataCommandExecutor extends SimpleCommandExecutor {

    @Inject
    private Config config;

    @Inject
    private PetBlockManager manager;

    @Inject
    public PetDataCommandExecutor(Config config, PluginContainer pluginContainer) {
        super(pluginContainer);
        this.register(config.getData("petblocks-gui"), builder -> {

        });
    }

    /**
     * Can be overwritten to listen to player executed commands.
     *
     * @param player player
     * @param args   args
     */
    @Override
    public void onPlayerExecuteCommand(Player player, CommandContext args) {
        if (!this.config.allowPetSpawning(player.getTransform()))
            return;
        if (args.hasAny("call")) {
            final Optional<SpongePetBlock> petBlock;
            if ((petBlock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
                petBlock.get().teleport(player.getTransform());
            }
        } else if (args.hasAny("toggle")) {
            final Optional<SpongePetBlock> optPetBlock;
            if ((optPetBlock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
                this.manager.getPetBlockController().remove(optPetBlock.get());
            } else {
                Task.builder().async().execute(() -> {
                    final Optional<PetMeta> optPetMeta = this.manager.getPetMetaController().getFromPlayer(player);
                    optPetMeta.ifPresent(petMeta -> Task.builder().execute(() -> {
                        final SpongePetBlock petBlock = this.manager.getPetBlockController().create(player, petMeta);
                        this.manager.getPetBlockController().store(petBlock);
                    }).submit(this.plugin));
                }).submit(this.plugin);
            }
        }/* else if (args.length >= 2 && args[0].equalsIgnoreCase("rename") && player.hasPermission(Permission.RENAMEPET.get())) {
            this.renameNameCommand(player, args);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("skin") && player.hasPermission(Permission.RENAMESKULL.get())) {
            this.handleNaming(player, args[1], true);
        }*/ else {
            this.manager.gui.open(player);
            Task.builder().async().execute(() -> {
                PetMeta petMeta;
                if ((petMeta = this.manager.getPetMetaController().getByPlayer(player)) == null) {
                    petMeta = this.manager.getPetMetaController().create(player);
                    this.manager.getPetMetaController().store(petMeta);
                }
                final PetMeta meta = petMeta;
                Task.builder().execute(() -> this.manager.gui.setPage(player, GUIPage.MAIN, meta)).submit(this.plugin);
            }).submit(this.plugin);
        }
    }

    private void renameNameCommand(Player player, String name) {
        try {
            final String message = name;
            if (message.length() > Config.getInstance().pet().getDesign_maxPetNameLength()) {
                player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getNamingErrorMessage()));
            } else {
                this.providePetblock(player, (meta, petBlock) -> {
                    //PetBlockModifyHelper.rename(meta, petBlock, message);
                    player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getNamingSuccessMessage()));
                    this.persistAsynchronously(meta);
                });
            }
        } catch (final Exception e) {
            player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getNamingErrorMessage()));
        }
    }

    private void renameSkull(Player player, String message) {
        if (message.length() > 20) {
            player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getSkullNamingErrorMessage()));
        } else {
            try {
                this.providePetblock(player, (meta, petBlock) -> {
                    meta.setDisplaySkin(CompatibilityItemType.SKULL_ITEM, 3, message, false);
                    this.persistAsynchronously(meta);
                    if (petBlock != null) {
                        petBlock.respawn();
                    }
                    player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getSkullNamingSuccessMessage()));
                });
            } catch (final Exception e) {
                player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getSkullNamingErrorMessage()));
            }
        }
    }

    private void providePetblock(Player player, PetRunnable<SpongePetBlock> runnable) {
        final Optional<SpongePetBlock> petBlock;
        if ((petBlock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
            runnable.run(petBlock.get().getMeta(), petBlock.get());
        } else {
            Task.builder().async().execute(() -> {
                if (!PetDataCommandExecutor.this.manager.getPetMetaController().hasEntry(player))
                    return;
                final PetMeta petMeta = PetDataCommandExecutor.this.manager.getPetMetaController().getByPlayer(player);
                Task.builder().execute(() -> runnable.run(petMeta, null)).submit(PetDataCommandExecutor.this.plugin);
            }).submit(this.plugin);
        }
    }

    private void persistAsynchronously(PetMeta petMeta) {
        Task.builder().async().execute(() -> this.manager.getPetMetaController().store(petMeta));
    }
}
