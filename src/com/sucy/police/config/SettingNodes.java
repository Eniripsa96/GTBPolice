package com.sucy.police.config;

public class SettingNodes {

    public static final String

        /**
         * Base node for plugin settings
         */
        BASE = "Settings.",

        /**
         * Amount of health bars players have
         */
        HEALTH = BASE + "health-bars",

        /**
         * Amount of bars to damage per baton hit
         */
        DAMAGE = BASE + "hit-damage",

        /**
         * Number of bars to restore upon regenerating
         */
        REGEN_AMOUNT = BASE + "regen-amount",

        /**
         * Number of seconds between regeneration ticks
         */
        REGEN_DELAY = BASE + "regen-delay",

        /**
         * Default value for the time in jail
         */
        DEFAULT_JAIL_TIME = BASE + "default-jail-time",

        /**
         * Stun duration for grenades
         */
        STUN_TIME = BASE + "stun-duration",

        /**
         * Default value for the broken rule
         */
        DEFAULT_RULE = BASE + "default-rule",

        /**
         * Default value for the report
         */
        DEFAULT_REPORT = BASE + "default-report",

        /**
         * Commands to issue upon jailing someone
         */
        PRE_COMMANDS = BASE + "pre-commands",

        /**
         * Commands to execute after sentencing someone to jail
         */
        JAIL_COMMANDS = BASE + "jail-commands",

        /**
         * Commands to execute after sentencing someone to prison
         */
        PRISON_WHITELIST = BASE + "prison-whitelist",

        /**
         * Cooldown for using log blocks
         */
        LOG_CD = BASE + "log-cd",

        /**
         * Base URL for law threads
         */
        LAW_URL = BASE + "law-url-base",

        /**
         * Base URL for categories
         */
        CAT_URL = BASE + "category-url-base",

        /**
         * Commands to perform when canceling a jailing
         */
        CANCEL_COMMANDS = BASE + "cancel-commands",

        /**
         * Commands to perform when using a sensor
         */
        SENSOR_COMMANDS = BASE + "sensor-commands",

        /**
         * Commands to perform while using a sensor while crouching
         */
        SHIFT_COMMANDS = BASE + "shift-sensor-commands",

        /**
         * Permissions needed for sensor commands
         */
        SENSOR_PERMISSIONS = BASE + "sensor-permissions",

        /**
         * Max sensor blocks per player
         */
        SENSOR_LIMIT = BASE + "sensor-limit";
}
