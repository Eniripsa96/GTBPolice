package com.sucy.police.command.wanted;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.wanted.WantedPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Command for displaying info on a wanted player
 */
public class CmdInfo implements ICommand {

    private static final String BREAK = ChatColor.DARK_GRAY + "-----------------------------------------------------";

    /**
     * Displays info on a wanted player
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires 1 arguments
        if (args.length == 1) {

            // Get the player
            WantedPlayer player = ((Police)plugin).getWantedManager().getPlayer(args[0]);

            // Player isn't on the wanted list
            if (player == null) {
                sender.sendMessage(ChatColor.DARK_RED + "That player is not on the wanted list");
            }

            // Show the details
            else {
                sender.sendMessage(BREAK);
                sender.sendMessage(ChatColor.GRAY + "Reported by: " + ChatColor.GOLD + player.getReporterName());
                sender.sendMessage(ChatColor.GRAY + "Crime: " + ChatColor.GOLD + player.getCrime());
                sender.sendMessage(BREAK);
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
        return PermissionNodes.INFO;
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
        return "Displays wanted info";
    }
}
