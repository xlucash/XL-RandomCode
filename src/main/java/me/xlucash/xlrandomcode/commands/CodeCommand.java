package me.xlucash.xlrandomcode.commands;

import me.xlucash.xlrandomcode.managers.CodeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CodeCommand implements CommandExecutor {

    private final CodeManager codeManager;

    public CodeCommand(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Komenda tylko dla graczy!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage("Użyj: /kod <kod>");
            return true;
        }

        if (!codeManager.isCodeActive()) {
            player.sendMessage("Nie ma aktywnego kodu!");
            return true;
        }

        String enteredCode = args[0];
        codeManager.playerEnteredCode(player, enteredCode);
        return true;
    }
}
