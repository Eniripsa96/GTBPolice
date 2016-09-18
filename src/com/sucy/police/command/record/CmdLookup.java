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
 * Command for showing the reports attached to a given player
 * or showing the full details of a single report
 */
public class CmdLookup implements ICommand {

    private static final String BREAK = ChatColor.DARK_GRAY + "-----------------------------------------------------";

    /**
     * Shows the list of reports attached to a player
     * or the full details of a single report
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires 1 argument for the list
        if (args.length == 1) {

            Police police = (Police)plugin;
            PlayerData player = police.getPlayerData(args[0]);
            ReportList reports = player.getReports();

            // No reports to show
            if (reports.isEmpty()) {
                sender.sendMessage(ChatColor.DARK_RED + "There are no records to show for that player");
            }

            // Display the list
            else {
                sender.sendMessage(BREAK);
                sender.sendMessage(ChatColor.GRAY + "Reports targeting the player: " + ChatColor.GOLD + player.getPlayerName());

                // Display each report
                for (Report report : reports.getList()) {
                    report.displayLookupListDetails(sender);
                }
                sender.sendMessage(BREAK);
            }
        }

        // Requires two arguments for the details
        else if (args.length == 2) {

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
                else reports.getReport(id).displayFullDetails(sender);
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
        return PermissionNodes.LOOKUP;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<player> [id]";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Displays reports";
    }
}
