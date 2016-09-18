package com.sucy.police.command.record;

import com.sucy.police.PermissionNodes;
import com.sucy.police.PlayerData;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.report.Report;
import com.sucy.police.report.ReportList;
import com.sucy.police.report.ReportStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Command for closing a report
 */
public class CmdClose implements ICommand {

    /**
     * Closes a report
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires two arguments
        if (args.length >= 3) {

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

                // Not open
                else if (reports.getReport(id).getStatus() != ReportStatus.OPEN) {
                    sender.sendMessage(ChatColor.DARK_RED + "That report is not open");
                }

                // Display the record
                else {
                    Report report = reports.getReport(id);
                    String punishment = args[2];
                    for (int i = 3 ; i < args.length; i++) {
                        punishment += " " + args[i];
                    }
                    report.close(punishment);
                    sender.sendMessage(ChatColor.DARK_GREEN + "The report has been closed");
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
        return PermissionNodes.CLOSE;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<player> <id> <result>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Closes a report";
    }
}
