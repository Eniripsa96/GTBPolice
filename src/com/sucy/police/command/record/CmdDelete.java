package com.sucy.police.command.record;

import com.sucy.police.PermissionNodes;
import com.sucy.police.PlayerData;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Command to display a list of all records
 */
public class CmdDelete implements ICommand {

    private static final String BREAK = ChatColor.DARK_GRAY + "-----------------------------------------------------";

    /**
     * Shows a list of all records
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires 2 arguments
        if (args.length == 2) {

            Police police = (Police)plugin;

            PlayerData player = police.getPlayerData(args[0]);

            // Invalid player
            if (player == null) {
                sender.sendMessage(ChatColor.DARK_RED + "No data exists for that player");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(args[1]);
            }

            // Invalid ID
            catch (Exception ex) {
                sender.sendMessage(ChatColor.DARK_RED + "Invalid record id");
                return;
            }

            boolean worked = player.deleteReport(id);

            // Worked
            if (worked) {
                sender.sendMessage(ChatColor.DARK_GREEN + "The record has been deleted");
            }

            // Invalid ID
            else sender.sendMessage(ChatColor.DARK_RED + "That player doesn't have a record with that ID");
        }
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.RECORD_DELETE;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<player> <id>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Deletes a report";
    }
}
