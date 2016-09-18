package com.sucy.police.command.wanted;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Command for adding players to the wanted list
 */
public class CmdAdd implements ICommand {

    /**
     * Adds a player to the wanted list
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires 3 arguments
        if (args.length == 3) {

            try {

                // Make sure the player name is valid
                if (args[0].length() > 16) {
                    sender.sendMessage(ChatColor.DARK_RED + "That is not a valid player name");
                    return;
                }

                boolean validLaw = true;

                // Spaces are not allowed
                if (!args[2].matches("[A-Za-z][0-9]+(,[A-Za-z][0-9]+)*")) {
                    validLaw = false;
                }

                // Split the law into multiple if necessary
                String[] rules;
                if (args[2].contains(",")) {
                    rules = args[2].split(",");
                }
                else rules = new String[] { args[2] };

                // Validate the laws
                for (String r : rules) {
                    String url = ((Police)plugin).getURL(r);
                    if (url == null) {
                        validLaw = false;
                    }
                }

                // Error message if one or more laws were invalid
                if (!validLaw) {
                    sender.sendMessage(ChatColor.DARK_RED + "That is not a valid law");
                    return;
                }

                int priority = Integer.parseInt(args[1]);

                // Add the player to the wanted list
                boolean worked = ((Police)plugin).getWantedManager().addPlayer(args[0], sender.getName(), args[2], priority);

                // Player was already on the wanted list
                if (!worked) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player is already on the wanted list");
                }

                // Player added successfully
                else {
                    sender.sendMessage(ChatColor.DARK_GREEN + "The player has been added to the wanted list");
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
        return PermissionNodes.ADD;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<player> <priority> <crime>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Adds a player";
    }
}
