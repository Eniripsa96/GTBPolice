package com.sucy.police.report;

import com.sucy.police.util.TextFormatter;
import org.bukkit.ChatColor;

/**
 * Possible states for the report
 */
public enum ReportStatus {

    /**
     * The report has yet to be resolved
     */
    OPEN,

    /**
     * The report has ended and punishment was issued
     */
    CLOSED,

    /**
     * The report has ended and no punishment was given
     */
    INVALIDATED;

    /**
     * @return formatted status name
     */
    public String getFormattedName() {
        return TextFormatter.format(name());
    }

    /**
     * @return color for the status in the record list
     */
    public String getColor() {
        return this == OPEN ? ChatColor.WHITE + ""
                : this == INVALIDATED ? ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH
                : ChatColor.GRAY + "";
    }
}