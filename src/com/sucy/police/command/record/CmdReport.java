package com.sucy.police.command.record;

import com.sucy.police.PermissionNodes;
import com.sucy.police.PlayerData;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.report.Report;
import com.sucy.police.report.ReportList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Command for displaying a detailed report to a requester
 */
public class CmdReport implements ICommand {

    /**
     * Displays a report to the sender
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires two arguments
        if (args.length == 2) {

            try {
                Police police = (Police)plugin;
                PlayerData player = police.getPlayerData(args[0]);
                ReportList reports = player.getReports();
                int id = Integer.parseInt(args[1]);

                // Invalid ID
                if (id < 1) {
                    sender.sendMessage(ChatColor.DARK_RED + "IDs must be positive integers");
                }

                // No record found
                else if (!reports.containsId(id)) {
                    sender.sendMessage(ChatColor.DARK_RED + "No report was found with that ID for that player");
                }

                // Display the record
                else {
                    reports.getReport(id).displayReport(sender);
                }
            }

            // Not a valid ID
            catch (Exception e) {
                sender.sendMessage(ChatColor.DARK_RED + "That is not a valid ID");
            }
        }

        // Not the right amount of args
        else handler.displayUsage(sender);
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.REPORT;
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
        return "Displays a report";
    }
}
