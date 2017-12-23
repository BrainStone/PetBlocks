package com.github.shynixn.petblocks.sponge.logic.business.commandexecutor;

import com.github.shynixn.petblocks.api.PetBlocksApi;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.business.enumeration.GUIPage;
import com.github.shynixn.petblocks.api.business.enumeration.Permission;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.google.inject.Inject;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class PetDataCommandExecutor extends SimpleCommandExecutor {

    @Inject
    private Config config;


    public void register()
    {
        this.register(config.getData("petblocks-gui"), builder -> {
            builder.arguments
                    (GenericArguments.onlyOne(GenericArguments.string(Text.of("call"))));
            builder.arguments
                    (GenericArguments.onlyOne(GenericArguments.string(Text.of("rename"))), GenericArguments.remainingJoinedStrings(Text.of("message")));
            builder.arguments
                    (GenericArguments.onlyOne(GenericArguments.string(Text.of("skin"))), GenericArguments.remainingJoinedStrings(Text.of("message")));

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
        if (!this.config.allowPetSpawning(player.getLocation()))
            return;
        if (args.hasAny("call")) {
            final Optional<PetBlock> petBlock;
            if ((petBlock = PetBlocksApi.getDefaultPetBlockController().getFromPlayer(player)).isPresent()) {
                petBlock.get().teleport(player.getLocation());
            }
        } else if (args.hasAny("toggle")) {
            if (PetBlocksApi.getDefaultPetBlockController().getByPlayer(player) != null) {
                PetBlocksApi.getDefaultPetBlockController().remove(PetBlocksApi.getDefaultPetBlockController().getByPlayer(player));
            } else {
                Task.builder().async().execute(() -> {
                    final PetMeta petMeta = PetBlocksApi.getDefaultPetMetaController().getByPlayer(player);
                    if (petMeta != null) {
                        Task.builder().execute(() -> {
                            final PetBlock petBlock = PetBlocksApi.getDefaultPetBlockController().create(player, petMeta);
                            PetBlocksApi.getDefaultPetBlockController().store(petBlock);
                        }).submit(this.plugin);
                    }
                }).submit(this.plugin);
            }
        }/* else if (args.length >= 2 && args[0].equalsIgnoreCase("rename") && player.hasPermission(Permission.RENAMEPET.get())) {
            this.renameNameCommand(player, args);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("skin") && player.hasPermission(Permission.RENAMESKULL.get())) {
            this.handleNaming(player, args[1], true);
        } else {
            this.manager.gui.open(player);
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                PetMeta petMeta;
                if ((petMeta = this.manager.getPetMetaController().getByPlayer(player)) == null) {
                    petMeta = this.manager.getPetMetaController().create(player);
                    this.manager.getPetMetaController().store(petMeta);
                }
                final PetMeta meta = petMeta;
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.manager.gui.setPage(player, GUIPage.MAIN, meta));
            });
        }*/
    }

 /*   private void handleNaming(Player player, String message, boolean skullNaming) {
        final PetBlock petBlock;
        if ((petBlock = PetBlocksApi.getDefaultPetBlockController().getByPlayer(player)) != null) {
            if (skullNaming) {
                this.renameSkull(player, message, petBlock.getMeta(), petBlock);
            }
        } else {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                final PetMeta petMeta = this.manager.getPetMetaController().getByPlayer(player);
                if (skullNaming) {
                    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.renameSkull(player, message, petMeta, null));
                }
            });
        }
    }

    private void renameNameCommand(Player player, String[] args) {
        try {
            final String message = this.mergeArgs(args, 1);
            if (message.length() > Config.getInstance().pet().getDesign_maxPetNameLength()) {
                player.sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getNamingErrorMessage());
            } else {
                this.providePetblock(player, (meta, petBlock) -> {
                    PetBlockModifyHelper.rename(meta, petBlock, message);
                    player.sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getNamingSuccessMessage());
                    this.persistAsynchronously(meta);
                });
            }
        } catch (final Exception e) {
            player.sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getNamingErrorMessage());
        }
    }

    private void renameSkull(Player player, String message, PetMeta petMeta, PetBlock petBlock) {
        if (message.length() > 20) {
            player.sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getSkullNamingErrorMessage());
        } else {
            try {
                petMeta.setSkin(Material.SKULL_ITEM.getId(), 3, message, false);
                this.persistAsynchronously(petMeta);
                if (petBlock != null)
                    petBlock.respawn();
                player.sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getSkullNamingSuccessMessage());
            } catch (final Exception e) {
                player.sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getSkullNamingErrorMessage());
            }
        }
    }

    private void providePetblock(Player player, PetRunnable runnable) {
        final PetBlock petBlock;
        if ((petBlock = this.manager.getPetBlockController().getByPlayer(player)) != null) {
            runnable.run(petBlock.getMeta(), petBlock);
        } else {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                if (!this.manager.getPetMetaController().hasEntry(player))
                    return;
                final PetMeta petMeta = this.manager.getPetMetaController().getByPlayer(player);
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> runnable.run(petMeta, null));
            });
        }
    }

    private String mergeArgs(String[] args, int up) {
        final StringBuilder builder = new StringBuilder();
        for (int i = up; i < args.length; i++) {
            if (builder.length() != 0) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    private void persistAsynchronously(PetMeta petMeta) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.manager.getPetMetaController().store(petMeta));
    }*/
}
