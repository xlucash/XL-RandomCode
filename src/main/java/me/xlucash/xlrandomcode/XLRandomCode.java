package me.xlucash.xlrandomcode;

import me.xlucash.xlrandomcode.commands.CodeCommand;
import me.xlucash.xlrandomcode.managers.CodeManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class XLRandomCode extends JavaPlugin {
    private CodeManager codeManager;

    @Override
    public void onEnable() {
        reloadConfig();
        codeManager = new CodeManager(this);

        getCommand("kod").setExecutor(new CodeCommand(codeManager));
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}
