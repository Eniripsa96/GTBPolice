package com.sucy.police.command.prison;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.PrisonState;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.config.Config;
import com.sucy.police.util.DataParser;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Command to release a player from prison
 */
public class CmdRelease implements ICommand {

    /**
     * Sets the starting point for prisons
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires one argument
        if (args.length == 1) {

            OfflinePlayer player = plugin.getServer().getPlayer(args[0]);
            Police police = (Police)plugin;

            // Player not online
            if (!player.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.DARK_RED + "That player has not played on this server");
            }

            // Player is not in prison
            else if (police.isInPrison(player.getName()) == PrisonState.NOT_IN_PRISON) {
                sender.sendMessage(ChatColor.DARK_RED + "That player is not in prison");
            }

            // Release point not set
            else if (police.getReleaseLoc() == null) {
                sender.sendMessage(ChatColor.DARK_RED + "The release point is not set");
            }

            // Release from prison
            else {
                police.freePrisoner(player.getName());
                sender.sendMessage(ChatColor.DARK_GREEN + "You have freed " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GREEN + " from prison");
            }
        }

        // Release location
        else if (args.length == 0 && sender instanceof Player) {
            Police police = (Police)plugin;
            police.setReleaseLoc(((Player) sender).getLocation());
            sender.sendMessage(ChatColor.DARK_GREEN + "The prison release point has been set to your location");
        }

        // Not the right number of arguments
        else {
            handler.displayUsage(sender);
        }
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.RELEASE;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<player>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Frees a prisoner";
    }
}
