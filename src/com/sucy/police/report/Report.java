package com.sucy.police.report;

import com.sucy.police.PlayerData;
import com.sucy.police.config.ReportNodes;
import com.sucy.police.util.TextSizer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A report created by jailing someone
 */
public class Report implements Comparable<Report> {

    private static final String BREAK = ChatColor.DARK_GRAY + "-----------------------------------------------------";

    public static final int
            NEWEST_FIRST = 1,
            OLDEST_FIRST = -1;
    public static int SORT_MODE = NEWEST_FIRST;

    private static final String
            JAILED = "Name: ",
            ID = "Report ID: ",
            DATE = "Date: ",
            JAILER = "Jailer: ",
            TIME = "Holding Time: ",
            CRIME = "Crime: ",
            CLOSED = "Closed: ",
            PUNISHMENT = "Punishment: ";

    private static final int
            JAILED_ID = 0,
            ID_ID = 1,
            DATE_ID = 2,
            JAILER_ID = 3,
            TIME_ID = 4,
            CRIME_ID = 5,
            CLOSED_ID = 6,
            PUNISHMENT_ID = 7;

    private static final List<String> FIELDS = TextSizer.expand(Arrays.asList(
            JAILED, ID, DATE, JAILER, TIME, CRIME, CLOSED, PUNISHMENT), true);

    private static final DateFormat FORMAT = new SimpleDateFormat("MM/dd/yy");
    private static final String DEFAULT_PUNISHMENT = "N/A";

    private final PlayerData playerData;
    private final String jailedPlayer;
    private final String jailerPlayer;
    private final String report;
    private final String crime;
    private final String date;
    private final int holdingTime;
    private final int id;

    private ReportStatus status;
    private String punishment;

    /**
     * Jail type constructor
     *
     * @param data   data of jailed player
     * @param jailed jailed player name
     * @param jailer jailer player name
     * @param report report summary
     * @param crime  crime name
     * @param time   time sentenced
     * @param court  whether or not court is needed
     */
    public Report(PlayerData data, String jailed, String jailer, String report, String crime, int time, boolean court) {
        this(data, jailed, jailer, report, crime, time, court ? ReportStatus.OPEN : ReportStatus.CLOSED);
    }

    /**
     * General constructor
     *
     * @param data   data of jailed player
     * @param jailed jailed player name
     * @param jailer jailer player name
     * @param report report summary
     * @param crime  crime name
     * @param time   time sentenced
     * @param status report status
     */
    private Report(PlayerData data, String jailed, String jailer, String report, String crime, int time, ReportStatus status) {
        this.playerData = data;
        this.status = status;
        this.jailedPlayer = jailed;
        this.jailerPlayer = jailer;
        this.report = report;
        this.crime = crime;
        this.date = FORMAT.format(Calendar.getInstance().getTime());
        this.holdingTime = time;
        this.id = playerData.getNextId();
        this.punishment = DEFAULT_PUNISHMENT;

        save();
    }

    /**
     * Constructor from config data
     *
     * @param data player data to load from
     * @param id   report id
     */
    public Report(PlayerData data, int id) {
        ConfigurationSection config = data.getConfig();

        this.playerData = data;
        String base = getBase(id);
        this.status = ReportStatus.valueOf(config.getString(base + ReportNodes.STATUS));
        this.jailedPlayer = config.getString(base + ReportNodes.JAILED);
        this.jailerPlayer = config.getString(base + ReportNodes.JAILER);
        this.report = config.getString(base + ReportNodes.REPORT);
        this.crime = config.getString(base + ReportNodes.CRIME);
        this.date = config.getString(base + ReportNodes.DATE);
        this.holdingTime = config.getInt(base + ReportNodes.TIME);
        this.punishment = config.getString(base + ReportNodes.PUNISHMENT);
        this.id = id;
    }

    /**
     * @return report status
     */
    public ReportStatus getStatus() {
        return status;
    }

    /**
     * @return name of jailed player
     */
    public String getJailedPlayerName() {
        return jailedPlayer;
    }

    /**
     * @return name of jailer
     */
    public String getJailerPlayerName() {
        return jailerPlayer;
    }

    /**
     * @return report summary
     */
    public String getReport() {
        return report;
    }

    /**
     * @return crime name
     */
    public String getCrime() {
        return crime;
    }

    /**
     * @return date the report was filed
     */
    public String getDate() {
        return date;
    }

    /**
     * @return holding time in minutes
     */
    public int getHoldingTime() {
        return holdingTime;
    }

    /**
     * @return report ID
     */
    public int getID() {
        return id;
    }

    /**
     * Retrieves the base node for the record
     *
     * @param id record id
     * @return   base node for the record
     */
    private String getBase(int id) {
        return id + ".";
    }

    /**
     * Opens the report up
     */
    public void open() {
        status = ReportStatus.OPEN;
        punishment = DEFAULT_PUNISHMENT;
        save();
    }

    /**
     * Closes the record while providing the punishment given
     *
     * @param punishment punishment given
     */
    public void close(String punishment) {
        status = ReportStatus.CLOSED;
        this.punishment = punishment;
        save();
    }

    /**
     * Invalidates the report
     */
    public void invalidate() {
        status = ReportStatus.INVALIDATED;
        save();
    }

    /**
     * Displays the report details to a requester
     *
     * @param sender requester
     */
    public void displayFullDetails(CommandSender sender) {

        sender.sendMessage(BREAK);

        // Display the details
        for (int i = 0; i < FIELDS.size(); i++) {

            String message = ChatColor.GRAY + FIELDS.get(i) + ChatColor.GOLD;

            // Get the appropriate field
            switch (i) {
                case JAILED_ID:
                    message += jailedPlayer;
                    break;
                case ID_ID:
                    message += id;
                    break;
                case DATE_ID:
                    message += date;
                    break;
                case JAILER_ID:
                    message += jailerPlayer;
                    break;
                case TIME_ID:
                    message += holdingTime + " minutes";
                    break;
                case CRIME_ID:
                    message += crime;
                    break;
                case CLOSED_ID:
                    message += status == ReportStatus.CLOSED ? "Yes" : "No";
                    break;
                case PUNISHMENT_ID:
                    message += punishment;
                    break;
            }

            // Send the line
            sender.sendMessage(message);
        }

        sender.sendMessage(BREAK);
    }

    /**
     * Displays the list details for the look up command
     *
     * @param sender target
     */
    public void displayLookupListDetails(CommandSender sender) {
        sender.sendMessage("(" + id + ") " + status.getColor() + date + " : " + crime);
    }

    /**
     * Displays the list details for the open command
     *
     * @param sender target
     */
    public void displayOpenListDetails(CommandSender sender) {
        sender.sendMessage(date + " " + jailedPlayer + " - " + id);
    }

    /**
     * Displays the list details for the list command
     *
     * @param sender target
     */
    public void displayListDetails(CommandSender sender) {
        sender.sendMessage(status.getColor() + date + " " + jailedPlayer + " - " + id);
    }

    /**
     * Displays the detailed report to a requester
     *
     * @param sender requester
     */
    public void displayReport(CommandSender sender) {
        sender.sendMessage(BREAK);
        sender.sendMessage(ChatColor.GRAY + "Accused: " + ChatColor.GOLD + jailedPlayer);
        sender.sendMessage(ChatColor.GRAY + "Accuser: " + ChatColor.GOLD + jailerPlayer);
        sender.sendMessage(ChatColor.GRAY + "Report: " + ChatColor.WHITE + report);
        sender.sendMessage(BREAK);
    }

    /**
     * Saves report data to a config section
     */
    public void save() {
        ConfigurationSection config = playerData.getConfig();

        String base = getBase(id);
        config.set(base + ReportNodes.STATUS, status.name());
        config.set(base + ReportNodes.JAILED, jailedPlayer);
        config.set(base + ReportNodes.JAILER, jailerPlayer);
        config.set(base + ReportNodes.REPORT, report);
        config.set(base + ReportNodes.CRIME, crime);
        config.set(base + ReportNodes.DATE, date);
        config.set(base + ReportNodes.TIME, holdingTime);
        config.set(base + ReportNodes.PUNISHMENT, punishment);

        playerData.saveConfig();
    }

    /**
     * Compares to other records by date
     *
     * @param report report to compare to
     * @return       date comparison
     */
    @Override
    public int compareTo(Report report) {
        try {

            // Status takes priority with Open coming first
            if (status != report.status) {
                if (status == ReportStatus.OPEN) return -SORT_MODE;
                if (report.status == ReportStatus.OPEN) return SORT_MODE;
                if (status == ReportStatus.INVALIDATED) return SORT_MODE;
                return -SORT_MODE;
            }

            // Otherwise sort by date
            else if (status == ReportStatus.OPEN) return FORMAT.parse(date).compareTo(FORMAT.parse(report.getDate())) * SORT_MODE;
            else return FORMAT.parse(date).compareTo(FORMAT.parse(report.getDate())) * -SORT_MODE;
        }

        // In case the date string is corrupt
        catch (Exception ex) {
            return 0;
        }
    }
}
