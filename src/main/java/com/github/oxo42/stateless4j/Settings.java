package com.github.oxo42.stateless4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {
    public static final String GUARD_IS_NULL = "guard is null";
    public static final String ENTRY_ACTION_IS_NULL = "entryAction is null";
    public static final String EXIT_ACTION_IS_NULL = "exitAction is null";
    public static final String ACTION_IS_NULL = "action is null";
    public static final String TRIGGER_IS_NULL = "trigger is null";
    public static final String DESTINATION_STATE_SELECTOR_IS_NULL = "destinationStateSelector is null";

    public static final Logger DEBUG_LOGGER = LoggerFactory.getLogger("Stateless4j");
    public static boolean LOG_TRANSITIONS = true,
            LOG_PARAMS = true,
            LOG_ENTRIES = true,
            LOG_EXITS = true;
}
