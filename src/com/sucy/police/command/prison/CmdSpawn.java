package com.sucy.police.command.prison;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to set the respawn point for prisons
 */
public class CmdSpawn implements ICommand {

    /**
     * Sets the respawn point for prisons
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Must be a player
        if (sender instanceof Player) {
            Police police = (Police)plugin;
            police.setPrisonSpawn(((Player) sender).getLocation());
            sender.sendMessage(ChatColor.DARK_GREEN + "The prison respawn location has been set to your location");
        }

        // Otherwise, send error message
        else sender.sendMessage(ChatColor.DARK_RED + "This can only be used by players");
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.RESPAWN;
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
        return "Sets prison respawn";
    }
}
