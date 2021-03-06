package com.github.shynixn.petblocks.sponge.logic.business.commandexecutor;

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.persistence.entity.EngineContainer;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.core.logic.business.entity.PetRunnable;
import com.github.shynixn.petblocks.core.logic.business.helper.ChatBuilder;
import com.github.shynixn.petblocks.core.logic.business.helper.ChatColor;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.PetBlocksManager;
import com.github.shynixn.petblocks.sponge.logic.business.helper.ExtensionMethodsKt;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public final class PetBlockCommandExecutor extends SimpleCommandExecutor {

    @Inject
    private PetBlocksManager manager;

    @Inject
    public PetBlockCommandExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
        this.register(Config.getInstance().getData("petblocks-configuration"), builder -> builder.arguments(GenericArguments.optionalWeak(GenericArguments.remainingRawJoinedStrings(Text.of("text")))));
    }

    /**
     * Can be overwritten to listener to all executed commands.
     *
     * @param sender sender
     * @param args   args
     */
    @Override
    protected void onCommandSenderExecuteCommand(CommandSource sender, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("engine") && sender instanceof Player && toIntOrNull(args[1]) != null)
            this.setEngineCommand((Player) sender, Integer.parseInt(args[1]));
        else if (args.length == 3 && args[0].equalsIgnoreCase("engine") && this.getOnlinePlayer(args[2]) != null && toIntOrNull(args[1]) != null)
            this.setEngineCommand(this.getOnlinePlayer(args[2]), Integer.parseInt(args[1]));
        else if (args.length == 3 && args[0].equalsIgnoreCase("costume") && sender instanceof Player && toIntOrNull(args[2]) != null)
            this.setCostumeCommand((Player) sender, args[1], Integer.parseInt(args[2]));
        else if (args.length == 4 && args[0].equalsIgnoreCase("costume") && this.getOnlinePlayer(args[3]) != null && toIntOrNull(args[2]) != null)
            this.setCostumeCommand(this.getOnlinePlayer(args[3]), args[1], Integer.parseInt(args[2]));
        else if (args.length == 1 && args[0].equalsIgnoreCase("enable") && sender instanceof Player)
            this.setPetCommand((Player) sender);
        else if (args.length == 2 && args[0].equalsIgnoreCase("enable") && this.getOnlinePlayer(args[1]) != null)
            this.setPetCommand(this.getOnlinePlayer(args[1]));
        else if (args.length == 1 && args[0].equalsIgnoreCase("disable") && sender instanceof Player)
            this.removePetCommand((Player) sender);
        else if (args.length == 2 && args[0].equalsIgnoreCase("disable") && this.getOnlinePlayer(args[1]) != null)
            this.removePetCommand(this.getOnlinePlayer(args[1]));
        else if (args.length == 1 && args[0].equalsIgnoreCase("toggle") && sender instanceof Player)
            this.togglePetCommand((Player) sender);
        else if (args.length == 2 && args[0].equalsIgnoreCase("toggle") && this.getOnlinePlayer(args[1]) != null)
            this.togglePetCommand(this.getOnlinePlayer(args[1]));
        else if (args.length >= 2 && args[0].equalsIgnoreCase("rename"))
            this.namePetCommand(sender, args);
        else if (args.length == 2 && args[0].equalsIgnoreCase("skin") && sender instanceof Player)
            this.changePetSkinCommand((Player) sender, args[1]);
        else if (args.length == 3 && args[0].equalsIgnoreCase("skin") && this.getOnlinePlayer(args[2]) != null)
            this.changePetSkinCommand(this.getOnlinePlayer(args[2]), args[1]);
        else if (args.length == 2 && args[0].equalsIgnoreCase("particle") && sender instanceof Player && toIntOrNull(args[1]) != null)
            this.setParticleCommand((Player) sender, Integer.parseInt(args[1]));
        else if (args.length == 3 && args[0].equalsIgnoreCase("particle") && this.getOnlinePlayer(args[2]) != null && toIntOrNull(args[1]) != null)
            this.setParticleCommand(this.getOnlinePlayer(args[2]), Integer.parseInt(args[1]));
        else if (args.length == 1 && args[0].equalsIgnoreCase("hat") && sender instanceof Player)
            this.hatPetCommand((Player) sender);
        else if (args.length == 2 && args[0].equalsIgnoreCase("hat") && this.getOnlinePlayer(args[1]) != null)
            this.hatPetCommand(this.getOnlinePlayer(args[1]));
        else if (args.length == 1 && args[0].equalsIgnoreCase("ride") && sender instanceof Player)
            this.ridePetCommand((Player) sender);
        else if (args.length == 2 && args[0].equalsIgnoreCase("ride") && this.getOnlinePlayer(args[1]) != null)
            this.ridePetCommand(this.getOnlinePlayer(args[1]));
        else if (args.length >= 2 && args[0].equalsIgnoreCase("item-name"))
            this.setSkullName(sender, args);
        else if (args.length >= 3 && args[0].equalsIgnoreCase("item-lore") && sender instanceof Player && toIntOrNull(args[1]) != null)
            this.setLore(sender, args);
        else if (args.length == 1 && args[0].equalsIgnoreCase("toggle-sound") && sender instanceof Player)
            this.toggleSounds((Player) sender);
        else if (args.length == 2 && args[0].equalsIgnoreCase("toggle-sound") && this.getOnlinePlayer(args[1]) != null)
            this.toggleSounds(this.getOnlinePlayer(args[2]));
        else if (args.length == 1 && args[0].equalsIgnoreCase("killnext") && sender instanceof Player && sender.hasPermission("petblocks.reload"))
            this.killNextCommand((Player) sender);
        else if (args.length == 1 && args[0].equalsIgnoreCase("3")) {
            ExtensionMethodsKt.sendMessage(sender, "");
            ExtensionMethodsKt.sendMessage(sender, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "                   PetBlocks " + "                       ");
            ExtensionMethodsKt.sendMessage(sender, "");
            this.sendMessage(sender, "hat [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Starts wearing the PetBlock."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "hat"
                    , this.getCommandName() + "hat " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "ride [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Starts riding the PetBlock."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "ride"
                    , this.getCommandName() + "ride " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "item-name <text> [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Changes the name of the PetBlock item when it is inside of the inventory of the player."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "item-name Petblock"
                    , this.getCommandName() + "item-name Amazing Beast"
                    , this.getCommandName() + "item-name My block " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "item-lore <line> <text> [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Changes the lore of the PetBlock item when it is inside of the inventory of the player."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "item-lore 1 Beast"
                    , this.getCommandName() + "item-lore 2 This is my pet"
                    , this.getCommandName() + "item-lore 2 PetBlock " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "killnext", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Kills the nearest entity to the player. Does not kill other players."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "killnext"
                    , ChatColor.GOLD + "<<Click me>>"});
            ExtensionMethodsKt.sendMessage(sender, "");
            ExtensionMethodsKt.sendMessage(sender, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌3/3┐                            ");
            ExtensionMethodsKt.sendMessage(sender, "");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("2")) {
            ExtensionMethodsKt.sendMessage(sender, "");
            ExtensionMethodsKt.sendMessage(sender, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "                   PetBlocks " + "                       ");
            ExtensionMethodsKt.sendMessage(sender, "");
            this.sendMessage(sender, "engine <number> [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Changes the engine being used of the PetBlock."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "engine 1"
                    , this.getCommandName() + "engine 2 " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "costume <category> <number> [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Changes the costume of the PetBlock."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "costume simple-blocks 1"
                    , this.getCommandName() + "costume simple-blocks 1 " + sender.getName()
                    , this.getCommandName() + "costume colored-blocks 2"
                    , this.getCommandName() + "costume player-heads 3"
                    , this.getCommandName() + "costume minecraft-heads 1"
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "rename <name> [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Renames the PetBlock."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "rename Beast"
                    , this.getCommandName() + "rename My awesome Pet"
                    , this.getCommandName() + "rename My Pet " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "skin <account/url> [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Replaces the costume of the PetBlock with the given skin."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "skin Shynixn"
                    , this.getCommandName() + "skin Shynixn " + sender.getName()
                    , this.getCommandName() + "skin http://textures.minecraft.net/texture/707dab2cbebea539b64d5ad246f9ccc1fcda7aa94b88e59fc2829852f46071"
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "particle <number> [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Changes the particle of the PetBlock."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "particle 2"
                    , this.getCommandName() + "particle 3 " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            ExtensionMethodsKt.sendMessage(sender, "");
            ExtensionMethodsKt.sendMessage(sender, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌2/3┐                            ");
            ExtensionMethodsKt.sendMessage(sender, "");
        } else if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("1"))) {
            ExtensionMethodsKt.sendMessage(sender, "");
            ExtensionMethodsKt.sendMessage(sender, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "                   PetBlocks " + "                       ");
            if (sender instanceof Player) {
                ExtensionMethodsKt.sendMessage(sender, "");
                ExtensionMethodsKt.sendMessage(sender, ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "Move your mouse over the commands to display tooltips!");
            }
            ExtensionMethodsKt.sendMessage(sender, "");
            this.sendMessage(sender, "enable [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Respawns the PetBlock of the given player."
                    , ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "enable"
                    , this.getCommandName() + "enable " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "disable [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Removes the PetBlock of the given player.",
                    ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "disable"
                    , this.getCommandName() + "disable " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "toggle [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Enables or disables the PetBlock.",
                    ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "toggle"
                    , this.getCommandName() + "toggle " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            this.sendMessage(sender, "toggle-sound [player]", new String[]{ChatColor.BLUE + "Description:" + ChatColor.RESET
                    , "Enables or disables the sounds of the PetBlock.",
                    ChatColor.YELLOW + "Examples:" + ChatColor.RESET
                    , this.getCommandName() + "toggle-sound"
                    , this.getCommandName() + "toggle-sound " + sender.getName()
                    , ChatColor.GOLD + "<<Click me>>"});
            ExtensionMethodsKt.sendMessage(sender, "");
            ExtensionMethodsKt.sendMessage(sender, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌1/3┐                            ");
            ExtensionMethodsKt.sendMessage(sender, "");
        }
    }

    private String getCommandName() {
        return '/' + this.getName() + ' ';
    }

    private void sendMessage(CommandSource commandSender, String message, String[] hoverText) {
        if (commandSender instanceof Player) {
            final StringBuilder builder = new StringBuilder();
            for (final String s : hoverText) {
                if (builder.length() != 0) {
                    builder.append('\n');
                }
                builder.append(s);
            }
            String fullCommand = (this.getCommandName() + message);
            if (fullCommand.contains("<")) {
                fullCommand = fullCommand.substring(0, fullCommand.indexOf("<"));
            } else if (fullCommand.contains("[")) {
                fullCommand = fullCommand.substring(0, fullCommand.indexOf("["));
            } else if (fullCommand.contains("-")) {
                fullCommand = fullCommand.substring(0, fullCommand.indexOf("-"));
            }
            ExtensionMethodsKt.sendMessage(new ChatBuilder()
                    .component(Config.getInstance().getPrefix() + this.getCommandName() + message)
                    .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, fullCommand)
                    .setHoverText(builder.toString())
                    .builder(), (Player) commandSender);
        } else {
            ExtensionMethodsKt.sendMessage(commandSender, Config.getInstance().getPrefix() + '/' + this.getName() + ' ' + message);
        }
    }

    private void setCostumeCommand(Player player, String category, int number) {
        this.providePet(player, (petMeta, petBlock) -> {
            final Optional<GUIItemContainer<Player>> item;
            if (category.equalsIgnoreCase("simple-blocks")) {
                item = Config.<Player>getInstance().getOrdinaryCostumesController().getContainerFromPosition(number);
            } else if (category.equalsIgnoreCase("colored-blocks")) {
                item = Config.<Player>getInstance().getColorCostumesController().getContainerFromPosition(number);
            } else if (category.equalsIgnoreCase("player-heads")) {
                item = Config.<Player>getInstance().getRareCostumesController().getContainerFromPosition(number);
            } else if (category.equalsIgnoreCase("minecraft-heads")) {
                item = Config.<Player>getInstance().getMinecraftHeadsCostumesController().getContainerFromPosition(number);
            } else {
                return;
            }
            item.ifPresent(guiItemContainer -> {
                ExtensionMethodsKt.setCostume(petMeta, petBlock, guiItemContainer);
                this.persistAsynchronously(petMeta);
            });
        });
    }

    private void setEngineCommand(Player player, int number) {
        this.providePet(player, (petMeta, petBlock) -> {
            final Optional<EngineContainer<GUIItemContainer<Player>>> optEngine = Config.<Player>getInstance().getEngineController().getContainerFromPosition(number);
            if (!optEngine.isPresent()) {
                ExtensionMethodsKt.sendMessage(player, Config.getInstance().getPrefix() + "Engine " + number + " could not be loaded correctly.");
            } else {
                ExtensionMethodsKt.setEngine(petMeta, petBlock, optEngine.get());
                this.persistAsynchronously(petMeta);
            }
        });
    }

    private void setParticleCommand(Player player, int number) {
        this.providePet(player, (petMeta, petBlock) -> {
            final Optional<GUIItemContainer<Player>> guiItemContainer = Config.<Player>getInstance().getParticleController().getContainerFromPosition(number);
            if (!guiItemContainer.isPresent()) {
                ExtensionMethodsKt.sendMessage(player, Config.getInstance().getPrefix() + "Particle not found.");
            } else {
                ExtensionMethodsKt.setParticleEffect(petMeta, petBlock, guiItemContainer.get());
                this.persistAsynchronously(petMeta);
            }
        });
    }

    private void toggleSounds(Player player) {
        this.providePet(player, (petMeta, petBlock) -> {
            petMeta.setSoundEnabled(!petMeta.isSoundEnabled());
            this.persistAsynchronously(petMeta);
        });
    }

    private void setLore(CommandSource commandSender, String[] args) {
        Player player = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }
        final Object[] mergedArgs = this.mergeArgs(args, 2);
        if (mergedArgs[1] != null) {
            player = (Player) mergedArgs[1];
        }
        final String text = (String) mergedArgs[0];
        final int line = Integer.parseInt(args[1]) - 1;
        this.providePet(player, (meta, petBlock) -> {
            if (petBlock != null && line >= 0) {
                final ArmorStand armorStand = (ArmorStand) petBlock.getArmorStand();
                final ItemStack itemStack = armorStand.getHelmet().get();

                final List<String> lore = new ArrayList<>(Arrays.asList(ExtensionMethodsKt.getLore(itemStack)));
                while (line >= lore.size()) {
                    lore.add("");
                }

                lore.set(line, ChatColor.translateAlternateColorCodes('&', text));
                ExtensionMethodsKt.setLore(itemStack, lore.toArray(new String[lore.size()]));
                armorStand.setHelmet(itemStack);
            }
        });
    }

    private void setSkullName(CommandSource commandSender, String[] args) {
        Player player = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }
        final Object[] mergedArgs = this.mergeArgs(args, 1);
        if (mergedArgs[1] != null) {
            player = (Player) mergedArgs[1];
        }
        final String text = (String) mergedArgs[0];
        if (player != null) {
            this.providePet(player, (meta, petBlock) -> {
                if (petBlock != null) {
                    final ArmorStand armorStand = (ArmorStand) petBlock.getArmorStand();
                    final ItemStack itemStack = armorStand.getHelmet().get();
                    ExtensionMethodsKt.setDisplayName(itemStack, text);
                    armorStand.setHelmet(itemStack);
                }
            });
        }
    }

    private void killNextCommand(Player sender) {
        double distance = 100;
        Entity nearest = null;
        for (final Entity entity : sender.getLocation().getExtent().getEntities()) {
            if (!(entity instanceof Player) && sender.getLocation().getPosition().distance(entity.getLocation().getPosition()) < distance) {
                distance = sender.getLocation().getPosition().distance(entity.getLocation().getPosition());
                nearest = entity;
            }
        }
        if (nearest != null) {
            nearest.remove();
            ExtensionMethodsKt.sendMessage(sender, Config.getInstance().getPrefix() + "" + ChatColor.GREEN + "You removed entity " + nearest.getType().getName() + '.');
        }
    }

    private void ridePetCommand(Player player) {
        final Optional<PetBlock> petBlock;
        if ((petBlock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
            petBlock.get().ride(player);
        }
    }

    private void hatPetCommand(Player player) {
        final Optional<PetBlock> petBlock;
        if ((petBlock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
            petBlock.get().wear(player);
        }
    }

    private void changePetSkinCommand(Player player, String skin) {
        try {
            this.providePet(player, (meta, petBlock) -> {
                ExtensionMethodsKt.setSkin(meta, petBlock, skin);
                this.persistAsynchronously(meta);
            });
        } catch (final Exception e) {
            ExtensionMethodsKt.sendMessage(player, Config.getInstance().getPrefix() + Config.getInstance().getSkullNamingErrorMessage());
        }
    }

    private void namePetCommand(CommandSource commandSender, String[] args) {
        try {
            Player player = null;
            if (commandSender instanceof Player) {
                player = (Player) commandSender;
            }
            final Object[] mergedArgs = this.mergeArgs(args, 1);
            if (mergedArgs[1] != null) {
                player = (Player) mergedArgs[1];
            }
            final String message = (String) mergedArgs[0];
            if (player != null) {
                if (message.length() > Config.getInstance().getDesign_maxPetNameLength()) {
                    ExtensionMethodsKt.sendMessage(commandSender, Config.getInstance().getPrefix() + Config.getInstance().getNamingErrorMessage());

                } else {
                    this.providePet(player, (meta, petBlock) -> {
                        ExtensionMethodsKt.rename(meta, petBlock, message);
                        this.persistAsynchronously(meta);
                    });
                }
            }
        } catch (final Exception e) {
            ExtensionMethodsKt.sendMessage(commandSender, Config.getInstance().getPrefix() + Config.getInstance().getNamingErrorMessage());
        }
    }

    private Object[] mergeArgs(String[] args, int up) {
        final StringBuilder builder = new StringBuilder();
        Player player = null;
        for (int i = up; i < args.length; i++) {
            if (i + 1 == args.length && Sponge.getServer().getPlayer(args[i]).isPresent()) {
                player = Sponge.getServer().getPlayer(args[i]).get();
                return new Object[]{builder.toString(), player};
            }
            if (builder.length() != 0) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return new Object[]{builder.toString(), player};
    }

    private void togglePetCommand(Player player) {
        this.providePet(player, (meta, petBlock) -> {
            if (petBlock == null) {
                this.setPetCommand(player);
            } else {
                this.removePetCommand(player);
            }
        });
    }

    private void setPetCommand(Player player) {
        this.removePetCommand(player);
        this.providePet(player, (meta, petBlock) -> {
            meta.setEnabled(true);
            this.persistAsynchronously(meta);
            final PetBlock petBlock1 = this.manager.getPetBlockController().create(player, meta);
            this.manager.getPetBlockController().store(petBlock1);
        });
    }

    private void removePetCommand(Player player) {
        final Optional<PetBlock> petBlock;
        if ((petBlock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
            this.manager.getPetBlockController().remove(petBlock.get());
        }
    }

    private Player getOnlinePlayer(String name) {
        for (final World world : Sponge.getGame().getServer().getWorlds()) {
            for (final Player player : world.getPlayers()) {
                if (player.getName().equals(name)) {
                    return player;
                }
            }
        }
        return null;
    }

    private void persistAsynchronously(PetMeta petMeta) {
        Task.builder().async().execute(() -> this.manager.getPetMetaController().store(petMeta)).submit(this.plugin);
    }

    private void providePet(Player player, PetRunnable runnable) {
        final Optional<PetBlock> petBlock;
        if ((petBlock = this.manager.getPetBlockController().getFromPlayer(player)).isPresent()) {
            runnable.run(petBlock.get().getMeta(), petBlock.get());
        } else {
            Task.builder().async().execute(() -> {
                final PetMeta petMeta;
                if (!this.manager.getPetMetaController().hasEntry(player)) {
                    final PetMeta petMeta2 = this.manager.getPetMetaController().create(player);
                    this.manager.getPetMetaController().store(petMeta2);
                }
                petMeta = this.manager.getPetMetaController().getFromPlayer(player).get();

                Task.builder().execute(() -> runnable.run(petMeta, null)).submit(this.plugin);
            }).submit(this.plugin);
        }
    }

    private static Integer toIntOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }
}
