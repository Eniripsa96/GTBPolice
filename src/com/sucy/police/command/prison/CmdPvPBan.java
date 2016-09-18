package com.sucy.police.command.prison;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
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
 * Bans a player from PvP
 */
public class CmdPvPBan implements ICommand {

    /**
     * Executes the command
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

            // Already banned
            else if (police.isPvPBanned(args[0])) {
                sender.sendMessage(ChatColor.DARK_RED + "That player is already banned from PvP");
            }

            // Ban them
            else {
                police.pvpBan(args[0]);
                sender.sendMessage(ChatColor.DARK_GREEN + "You have banned them from PvP");
                if (player.isOnline()) {
                    police.getServer().getPlayer(player.getName()).sendMessage(ChatColor.DARK_RED + "You have been banned from PvP");
                }
            }
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
        return PermissionNodes.PVP_BAN;
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
        return "Bans from PvP";
    }
}
