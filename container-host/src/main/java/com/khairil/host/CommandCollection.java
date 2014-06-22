package com.khairil.host;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This final collection defines the possible commands.
 * 
 */
public final class CommandCollection {

    private CommandCollection() {
    }

    protected static final String CREATE = "create";
    protected static final String START = "start";
    protected static final String STOP = "stop";
    protected static final String UPDATE = "update";
    protected static final String DELETE = "delete";
    protected static final String LIST_IMAGE = "list-image";
    protected static final String SAVE_AS = "save-as";

    private static Set<String> allCommands;

    static {
        allCommands = new LinkedHashSet<String>();
        Collections.addAll(allCommands, CREATE, START, STOP, UPDATE, DELETE, LIST_IMAGE, SAVE_AS);
    }

    /**
     * Returns a set of all command values.
     * 
     * @return
     */
    protected static Set<String> values() {
        return allCommands;
    }
}
