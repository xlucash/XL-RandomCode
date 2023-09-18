package me.xlucash.xlrandomcode.models;

public class Reward {
    private final String displayName;
    private final String command;

    public Reward(String displayName, String command) {
        this.displayName = displayName;
        this.command = command;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCommand() {
        return command;
    }
}
