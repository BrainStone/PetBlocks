package com.github.shynixn.petblocks.sponge.logic.business.commandexecutor;

import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

@Singleton
public final class PetBlockReloadCommandExecutor extends SimpleCommandExecutor {

    @Inject
    private Config config;

    @Inject
    public PetBlockReloadCommandExecutor(PluginContainer plugin) {
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
        this.config.reload();
        player.sendMessage(this.config.getPrefix().concat(Text.of("Reloaded PetBlocks.")));
    }
}
