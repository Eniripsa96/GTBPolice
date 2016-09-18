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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Command for displaying the list of open reports
 */
public class CmdOpen implements ICommand {

    private static final String BREAK = ChatColor.DARK_GRAY + "-----------------------------------------------------";

    /**
     * Shows the list of all open records
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Open a closed report
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

                // Not open
                else if (reports.getReport(id).getStatus() == ReportStatus.OPEN) {
                    sender.sendMessage(ChatColor.DARK_RED + "That report is already open");
                }

                // Display the record
                else {
                    Report report = reports.getReport(id);
                    report.open();
                    sender.sendMessage(ChatColor.DARK_GREEN + "The report has been opened");
                }
            }

            // Not a valid ID
            catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.DARK_RED + "That is not a valid ID");
            }

            return;
        }

        // Get the page to display
        int page = 1;
        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                // do nothing
            }
        }

        Police police = (Police)plugin;
        ArrayList<Report> reports = new ArrayList<Report>();
        for (PlayerData player : police.getPlayerData()) {
            player.getReports().addOpenTo(reports);
        }

        // Sort the reports
        Report.SORT_MODE = Report.OLDEST_FIRST;
        Collections.sort(reports);
        Report.SORT_MODE = Report.NEWEST_FIRST;

        // Limit the page number
        int maxPage = (reports.size() + 6) / 7;
        if (maxPage < 1) maxPage = 1;
        if (page > maxPage) {
            page = maxPage;
        }
        if (page < 1) {
            page = 1;
        }

        // Display the list
        sender.sendMessage(BREAK);
        sender.sendMessage(ChatColor.DARK_GREEN + "Open Report List - (Page " + page + "/" + maxPage + ")");
        if (maxPage == 0) {
            sender.sendMessage(ChatColor.GRAY + "There are no reports to view");
        }
        for (int i = page * 7 - 7; i < page * 7 && i < reports.size(); i++) {
            reports.get(i).displayOpenListDetails(sender);
        }
        sender.sendMessage(BREAK);
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.OPEN;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "[player] [id]";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Displays open reports";
    }
}
