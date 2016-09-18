package com.sucy.police.report;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * List of reports loaded from player data
 */
public class ReportList {

    private final ArrayList<Report> reports = new ArrayList<Report>();
    private final int offset;

    /**
     * Constructor
     *
     * @param offset number of deleted reports
     */
    public ReportList(int offset) {
        this.offset = offset;
    }

    /**
     * Adds a report to the list
     *
     * @param report report to add
     */
    public void add(Report report) {
        reports.add(report);
    }

    /**
     * Checks if the list contains a report with the id
     *
     * @param id id to check for
     * @return   true if contains it, false otherwise
     */
    public boolean containsId(int id) {
        if (id <= 0 || id > reports.size() + offset) return false;

        for (Report report : reports) {
            if (report.getID() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the report of the designated ID
     *
     * @param id report ID
     * @return   report with the ID of null if not found
     */
    public Report getReport(int id) {
        if (id <= 0 || id > reports.size() + offset) return null;

        for (Report report : reports) {
            if (report.getID() == id) {
                return report;
            }
        }
        return null;
    }

    /**
     * Displays the list of commands to the requester
     *
     * @param sender requester
     */
    public void displayList(CommandSender sender) {
        for (Report report : reports) {
            sender.sendMessage(report.getStatus().getColor() +
                    (report.getStatus() == ReportStatus.INVALIDATED ? "" + ChatColor.STRIKETHROUGH : "")
                    + "(" + report.getID() + ") " + report.getDate() + ": " + report.getCrime());
        }
    }

    /**
     * Sorts the reports
     */
    public void addTo(List<Report> list) {
        list.addAll(reports);
    }

    /**
     * Adds all open reports to the list
     *
     * @param list list to add to
     */
    public void addOpenTo(List<Report> list) {
        for (Report report : reports) {
            if (report.getStatus() == ReportStatus.OPEN) {
                list.add(report);
            }
        }
    }

    /**
     * Gets the contained list of reports
     *
     * @return report list
     */
    public List<Report> getList() {
        return reports;
    }

    /**
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return reports.size() == 0;
    }
}
