package com.sucy.police.command.wanted;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CmdList implements ICommand {

    /**
     * Displays the wanted list
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Needs to be a player
        if (sender instanceof Player) {

            Police police = (Police)plugin;

            // No entries to show
            if (!police.getWantedManager().hasEntries()) {
                sender.sendMessage(ChatColor.DARK_RED + "There are no entries on the wanted list to view");
                return;
            }

            // Show the wanted list
            boolean worked = police.getWantedManager().viewScoreboard((Player)sender);

            // Already is viewing the scoreboard
            if (!worked) {
                sender.sendMessage(ChatColor.DARK_RED + "You are already viewing the wanted list");
            }

            // Wanted list is now shown
            else {
                sender.sendMessage(ChatColor.DARK_GREEN + "You are now viewing the wanted list");
            }
        }

        // Not a player
        else {
            sender.sendMessage(ChatColor.DARK_RED + "This command cannot be used by the console");
        }
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.LIST;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Displays wanted list";
    }
}
