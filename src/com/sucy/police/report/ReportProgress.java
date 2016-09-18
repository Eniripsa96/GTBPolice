package com.sucy.police.report;

import com.sucy.police.Police;
import org.bukkit.entity.Player;

/**
 * A report created by a jailer
 */
public class ReportProgress {

    private final Police plugin;
    private final String playerName;
    private final String targetName;

    private ProgressState state;
    private int time;
    private String rule;
    private String report;
    private boolean court;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param player player name
     */
    public ReportProgress(Police plugin, String player, String target) {
        this.plugin = plugin;
        this.playerName = player;
        this.targetName = target;
        this.state = ProgressState.COURT;
        court = false;
    }

    /**
     * @return type of the report
     */
    public boolean isOpen() {
        return court;
    }

    /**
     * @return state of the report process
     */
    public ProgressState getState() {
        return state;
    }

    /**
     * @return name of player filing the report
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return player filing the report
     */
    public Player getPlayer() {
        return plugin.getServer().getPlayer(playerName);
    }

    /**
     * @return name of the player being jailed
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * @return player being jailed
     */
    public Player getTarget() {
        return plugin.getServer().getPlayer(targetName);
    }

    /**
     * @return time provided by the report
     */
    public int getTime() {
        return time == 0 ? plugin.getIntSetting(ProgressState.TIME.getSettingsNode()) : time;
    }

    /**
     * @return rule provided by the report
     */
    public String getRule() {
        return rule == null ? plugin.getStringSetting(ProgressState.RULE.getSettingsNode()) : rule;
    }

    /**
     * @return report provided by the report <- yup
     */
    public String getReport() {
        return report == null ? plugin.getStringSetting(ProgressState.REPORT.getSettingsNode()) : report;
    }

    /**
     * Applies a response from the player
     *
     * @param answer response from the player
     * @return       true if valid input, false otherwise
     */
    public boolean applyResponse(String answer) {
        switch (state) {
            case COURT:
                return applyCourt(answer);
            case TIME:
                return applyTime(answer);
            case RULE:
                return applyRule(answer);
            case REPORT:
                return applyReport(answer);
            default:
                return true;
        }
    }

    /**
     * Applies the answer to the court requirement
     *
     * @param court yes or no answer
     * @return      true if valid input, false otherwise
     */
    private boolean applyCourt(String court) {

        // Answered yes
        if (court.equalsIgnoreCase("yes")) {
            this.court = true;
            state = ProgressState.TIME;
            return true;
        }

        // Answered no
        else if (court.equalsIgnoreCase("no")) {
            this.court = false;
            state = ProgressState.TIME;
            return true;
        }

        // Invalid response
        else return false;
    }

    /**
     * Applies the answer to the jail time
     *
     * @param time jail time in minutes
     * @return     true if valid input, false otherwise
     */
    private boolean applyTime(String time) {

        // Try to parse the time
        try {
            int t = Integer.parseInt(time);

            // Must be within the range 1 <= t <= 60
            if (t < 1 || t > 60) {
                return false;
            }

            this.time = t;
            state = ProgressState.RULE;
            return true;
        }

        // Not a number
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Applies the answer to the broken rule
     *
     * @param rule rule that was broken
     * @return     true if valid response, false otherwise
     */
    private boolean applyRule(String rule) {

        // Spaces are not allowed
        if (!rule.matches("[A-Za-z][0-9]+(,[A-Za-z][0-9]+)*")) {
            return false;
        }

        String[] rules;
        if (rule.contains(",")) {
            rules = rule.split(",");
        }
        else rules = new String[] { rule };

        for (String r : rules) {
            String url = plugin.getURL(r);
            if (url == null) {
                return false;
            }
        }

        // Set the rule
        this.rule = rule;
        state = ProgressState.REPORT;
        return true;
    }

    /**
     * Applies a response to a report
     *
     * @param report report details
     * @return       true if a valid response, false otherwise
     */
    private boolean applyReport(String report) {

        // Cannot be a null entry
        if (report == null) {
            return false;
        }

        // Cannot be an empty string
        else if (report.length() == 0) {
            return false;
        }

        // Set the report
        else {
            this.report = report;
            state = ProgressState.FINISHED;
            return true;
        }
    }
}
