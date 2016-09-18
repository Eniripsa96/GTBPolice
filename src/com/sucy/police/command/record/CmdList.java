package com.sucy.police.command.record;

import com.sucy.police.PermissionNodes;
import com.sucy.police.PlayerData;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.report.Report;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Command to display a list of all records
 */
public class CmdList implements ICommand {

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

        // Get the page to display
        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                // do nothing
            }
        }

        // Get the reports
        Police police = (Police)plugin;
        ArrayList<Report> reports = new ArrayList<Report>();
        for (PlayerData player : police.getPlayerData()) {
            player.getReports().addTo(reports);
        }

        // Sort the reports
        Collections.sort(reports);

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
        sender.sendMessage(ChatColor.DARK_GREEN + "Report List - (Page " + page + "/" + maxPage + ")");
        for (int i = page * 7 - 7; i < page * 7 && i < reports.size(); i++) {
            reports.get(i).displayListDetails(sender);
        }
        sender.sendMessage(BREAK);
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.RECORD_LIST;
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
        return "Displays all reports";
    }
}
