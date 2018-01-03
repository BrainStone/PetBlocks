package com.github.shynixn.petblocks.sponge.logic.business.commandexecutor;

import com.github.shynixn.petblocks.api.business.enumeration.GUIPage;
import com.github.shynixn.petblocks.api.business.enumeration.Permission;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.sponge.entity.SpongePetBlock;
import com.github.shynixn.petblocks.sponge.logic.business.PetBlockManager;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.helper.SpongePetBlockModifyHelper;
import com.google.inject.Inject;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class PetDataCommandExecutor extends SimpleCommandExecutor {

    @Inject
    private PetBlockManager manager;

    @Inject
    public PetDataCommandExecutor(Config config, PluginContainer pluginContainer, ToggleCommandExecutor toggleCommandExecutor,
                                  RenameCommandExecutor renameCommandExecutor, RenameSkinCommandExecutor renameSkinCommandExecutor,
                                  CallCommandExecutor callCommandExecutor) {
        super(pluginContainer);
        this.register(config.getData("petblocks-gui"), builder -> {
            final CommandSpec.Builder toggleCommandBuilder = CommandSpec.builder()
                    .executor(toggleCommandExecutor);
            final CommandSpec.Builder callCommandBuilder = CommandSpec.builder()
                    .executor(callCommandExecutor);
            final CommandSpec.Builder renameCommandBuilder = CommandSpec.builder()
                    .permission(Permission.RENAMEPET.get())
                    .arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
                    .executor(renameCommandExecutor);
            final CommandSpec.Builder setSkinCommandBuilder = CommandSpec.builder()
                    .permission(Permission.RENAMESKULL.get())
                    .arguments(GenericArguments.string(Text.of("skin")))
                    .executor(renameSkinCommandExecutor);
            builder.child(toggleCommandBuilder.build(), "toggle");
            builder.child(renameCommandBuilder.build(), "rename");
            builder.child(setSkinCommandBuilder.build(), "skin");
            builder.child(callCommandBuilder.build(), "call");
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

    private static class CallCommandExecutor extends SimpleCommandExecutor {
        @Inject
        private PetBlockManager manager;

        @Inject
        public CallCommandExecutor(PluginContainer plugin) {
            super(plugin);
        }

        /**
         * Can be overwritten to listen to player executed commands.
         *
         * @param player player
         * @param args   args
         */
        @Override
        public void onPlayerExecuteCommand(Player player, CommandContext args) {
            final Optional<SpongePetBlock> petBlock;
            if ((petBlock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
                petBlock.get().teleport(player.getTransform());
            }
        }
    }

    private static class ToggleCommandExecutor extends SimpleCommandExecutor {
        @Inject
        private PetBlockManager manager;

        @Inject
        public ToggleCommandExecutor(PluginContainer plugin) {
            super(plugin);
        }

        /**
         * Can be overwritten to listen to player executed commands.
         *
         * @param player player
         * @param args   args
         */
        @Override
        public void onPlayerExecuteCommand(Player player, CommandContext args) {
            if (!Config.getInstance().allowPetSpawning(player.getTransform()))
                return;
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
        }
    }

    private static class RenameCommandExecutor extends SimpleCommandExecutor {

        @Inject
        private PetBlockManager manager;

        @Inject
        public RenameCommandExecutor(PluginContainer plugin) {
            super(plugin);
        }

        /**
         * Can be overwritten to listen to player executed commands.
         *
         * @param player player
         * @param args   args
         */
        @Override
        public void onPlayerExecuteCommand(Player player, CommandContext args) {
            try {
                final String message = args.<String>getOne("name").get();
                if (message.length() > Config.getInstance().pet().getDesign_maxPetNameLength()) {
                    player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getNamingErrorMessage()));
                } else {
                    this.manager.providePetblockData(player, (meta, petBlock) -> {
                        SpongePetBlockModifyHelper.rename(meta, petBlock, message);
                        player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getNamingSuccessMessage()));
                        this.manager.getPetMetaController().storeAsynchronly(meta);
                    });
                }
            } catch (final Exception e) {
                player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getNamingErrorMessage()));
            }
        }
    }

    private static class RenameSkinCommandExecutor extends SimpleCommandExecutor {

        private static int MAX_PETSKINLENGTH = 20;

        @Inject
        private PetBlockManager manager;

        @Inject
        public RenameSkinCommandExecutor(PluginContainer plugin) {
            super(plugin);
        }

        /**
         * Can be overwritten to listen to player executed commands.
         *
         * @param player player
         * @param args   args
         */
        @Override
        public void onPlayerExecuteCommand(Player player, CommandContext args) {
            try {
                final String message = args.<String>getOne("name").get();
                if (message.length() > MAX_PETSKINLENGTH) {
                    player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getSkullNamingErrorMessage()));
                } else {
                    this.manager.providePetblockData(player, (meta, petBlock) -> {
                        SpongePetBlockModifyHelper.setSkin(meta, petBlock, message);
                        player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getSkullNamingSuccessMessage()));
                        this.manager.getPetMetaController().storeAsynchronly(meta);
                    });
                }
            } catch (final Exception e) {
                player.sendMessage(Config.getInstance().getPrefix().concat(Config.getInstance().getSkullNamingErrorMessage()));
            }
        }
    }
}
