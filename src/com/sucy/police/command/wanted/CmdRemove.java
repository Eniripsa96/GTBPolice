package com.sucy.police.command.wanted;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Command for removing a player from the wanted list
 */
public class CmdRemove implements ICommand {

    /**
     * Removes a player from the wanted list
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires 1 argument
        if (args.length == 1) {

            try {

                // Remove the player from the wanted list
                boolean worked = ((Police)plugin).getWantedManager().removePlayer(args[0]);

                // Player was not on the wanted list
                if (!worked) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player is not on the wanted list");
                }

                // Player removed successfully
                else {
                    sender.sendMessage(ChatColor.DARK_GREEN + "The player has been removed from the wanted list");
                }
            }

            // Invalid priority
            catch (Exception e) {
                sender.sendMessage(ChatColor.DARK_RED + "That is not a valid priority");
            }
        }

        // Not the right amount of arguments
        else handler.displayUsage(sender);
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.REMOVE_WANTED;
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
        return "Removes a player";
    }
}
