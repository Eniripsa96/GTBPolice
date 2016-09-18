package com.sucy.police.report;

import com.sucy.police.config.SettingNodes;
import org.bukkit.ChatColor;

/**
 * States of the progress in filling out a report
 */
public enum ProgressState {

    /**
     * Whether or not a court case is requested
     */
    COURT (1, ChatColor.DARK_GREEN + "Does this person need a court case?", "Please enter \"" + ChatColor.GOLD + "yes" + ChatColor.DARK_GREEN + "\" or \"" + ChatColor.GOLD + "no" + ChatColor.DARK_GREEN + "\".", "no"),

    /**
     * Requesting the time to jail the player
     */
    TIME (2, ChatColor.DARK_GREEN + "How long should the criminal be held?", ChatColor.DARK_GREEN + "Please enter a value between 1 and 60.", SettingNodes.DEFAULT_JAIL_TIME),

    /**
     * Requesting the rule the player broke
     */
    RULE (3, ChatColor.DARK_GREEN + "What rule did this person break? Use " + ChatColor.GOLD + "/law list " + ChatColor.LIGHT_PURPLE + "[category]" + ChatColor.DARK_GREEN + "if you don't remember the law codes. Type \"none\" if the user did not break a rule. For multiple rules, separate them with a comma.", ChatColor.DARK_GREEN + "Please enter valid law codes with no spaces.", SettingNodes.DEFAULT_RULE),

    /**
     * Requesting the player to provide a report
     */
    REPORT (4, ChatColor.DARK_GREEN + "You have been given a book to fill out the detailed report. When you are done, the report will be filed.", ChatColor.DARK_RED + "Please include something for the detailed report.", SettingNodes.DEFAULT_REPORT),

    /**
     * Finishing the report
     */
    FINISHED (5, ChatColor.DARK_GREEN + "Thank you, the jailing process is now complete.", null, null),

    ;

    private final int part;
    private final String question;
    private final String errorQuestion;
    private final String settingsNode;

    /**
     * Enum constructor
     *
     * @param part          ID of the state
     * @param question      question attached to the state
     * @param errorQuestion question asked when the first response is invalid
     * @param settingsNode  settings node for the default value of the state
     */
    private ProgressState(int part, String question, String errorQuestion, String settingsNode) {
        this.part = part;
        this.question = question;
        this.errorQuestion = errorQuestion;
        this.settingsNode = settingsNode;
    }

    /**
     * @return Setting containing the default value
     */
    public String getSettingsNode() {
        return settingsNode;
    }

    /**
     * @return part ID of the question
     */
    public int getPart() {
        return part;
    }

    /**
     * @return question associated with the state
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @return question provided when the first response is invalid
     */
    public String getErrorQuestion() {
        return errorQuestion;
    }
}