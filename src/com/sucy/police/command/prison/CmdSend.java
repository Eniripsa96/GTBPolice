package com.sucy.police.command.prison;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.PrisonState;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to send a player to prison
 */
public class CmdSend implements ICommand {

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
        if (args.length == 1 || args.length == 2) {

            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            Police police = (Police)plugin;

            // Get the duration
            int m = 0;
            long duration = 0;
            if (args.length == 2) {
                char unit = args[1].charAt(args[1].length() - 1);
                if (unit == 's') m = 1000;
                else if (unit == 'm') m = 60 * 1000;
                else if (unit == 'h') m = 60 * 60 * 1000;
                else if (unit == 'd') m = 24 * 60 * 60 * 1000;
                args[1] = args[1].substring(0, args.length - 1);
                try {
                    duration = m * Integer.parseInt(args[1]);
                }
                catch (Exception ex) { /* Do nothing */ }
            }
            else duration = Long.MAX_VALUE;

            // Invalid time
            if (duration <= 0) {
                sender.sendMessage(ChatColor.DARK_RED + "That is not a valid time");
            }

            // Player not online
            else if (!player.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.DARK_RED + "That player has not played on this server before");
            }

            // Player is already in prison
            else if (police.isInPrison(player.getName()) != PrisonState.NOT_IN_PRISON) {
                sender.sendMessage(ChatColor.DARK_RED + "That player is already in prison");
            }

            // Spawn or respawn not set
            else if (police.getPrisonLoc() == null || police.getPrisonSpawn() == null) {
                sender.sendMessage(ChatColor.DARK_RED + "The spawn points for the prison are not set");
            }

            // Send to prison
            else {
                police.addPrisoner(player, duration);
                sender.sendMessage(ChatColor.DARK_GREEN + "You have sent " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GREEN + " to prison");

                if (player.isOnline()) {
                    plugin.getServer().getPlayer(player.getName()).sendMessage(ChatColor.DARK_GREEN + "You have been sent to prison");
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
        return PermissionNodes.SEND;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<player> <time>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Sends player to prison";
    }
}
