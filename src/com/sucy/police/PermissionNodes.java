package com.sucy.police;

/**
 * Permissions for the plugin
 */
public class PermissionNodes {

    public static final String

        /**
         * Root node for all permissions
         */
        POLICE_BASE = "police.",

        /**
         * Root node for record permissions
         */
        RECORD_BASE = POLICE_BASE + "record.",

        /**
         * Root node for wanted permissions
         */
        WANTED_BASE = POLICE_BASE + "wanted.",

        /**
         * Root node for prison permissions
         */
        PRISON_BASE = POLICE_BASE + "prison.",

        /**
         * Placing and viewing sensor blocks
         */
        SENSOR = POLICE_BASE + "sensor",

        /**
         * Root node for law commands
         */
        LAW_BASE = POLICE_BASE + "law.",

        /**
         * Hitting other people with a baton to jail them
         */
        BATON = POLICE_BASE + "baton",

        /**
         * Stun grenades
         */
        STUN = POLICE_BASE + "stun",

        /**
         * Record Report command
         */
        REPORT = RECORD_BASE + "report",

        /**
         * Record Lookup command
         */
        LOOKUP = RECORD_BASE + "lookup",

        /**
         * Record remove command
         */
        REMOVE = RECORD_BASE + "remove",

        /**
         * Record close command
         */
        CLOSE = RECORD_BASE + "close",

        /**
         * Record open command
         */
        OPEN = RECORD_BASE + "open",

        /**
         * Record list command
         */
        RECORD_LIST = RECORD_BASE + "list",

        /**
         * Deletes a record
         */
        RECORD_DELETE = RECORD_BASE + "delete",

        /**
         * Displays the wanted list
         */
        LIST = WANTED_BASE + "list",

        /**
         * Adds people to the wanted list
         */
        ADD = WANTED_BASE + "add",

        /**
         * Removes people from the wanted list
         */
        REMOVE_WANTED = WANTED_BASE + "remove",

        /**
         * Displays wanted info
         */
        INFO = WANTED_BASE + "info",

        /**
         * Sets the start point for prison
         */
        START = PRISON_BASE + "start",

        /**
         * Sets the respawn point for prison
         */
        RESPAWN = PRISON_BASE + "respawn",

        /**
         * Sends a player to prison
         */
        SEND = PRISON_BASE + "send",

        /**
         * Releases a player from prison
         */
        RELEASE = PRISON_BASE + "release",

        /**
         * Viewing prison inventories
         */
        INVENTORY = PRISON_BASE + "inventory",

        /**
         * Ban players from pvp
         */
        PVP_BAN = PRISON_BASE + "pvpban",

        /**
         * Displaying the list of laws
         */
        LIST_LAW = LAW_BASE + "list",

        /**
         * Adds a new law to the list
         */
        ADD_LAW = LAW_BASE + "add",

        /**
         * Create new categories
         */
        CATEGORY = LAW_BASE + "category",

        /**
         * Removes laws
         */
        REMOVE_LAW = LAW_BASE + "remove",

        /**
         * Set URLS of laws
         */
        EDIT = LAW_BASE + "edit";
}