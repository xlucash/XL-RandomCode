package me.xlucash.xlrandomcode.managers;

import me.xlucash.xlrandomcode.XLRandomCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class CodeManager {
    private final XLRandomCode plugin;
    private String currentCode;
    private boolean isCodeActive = false;

    public CodeManager(XLRandomCode plugin) {
        this.plugin = plugin;
        scheduleNextCode();
    }

    public void scheduleNextCode() {
        FileConfiguration config = plugin.getConfig();
        long codeTimeIntervals = config.getInt("time-delay-in-seconds") * 20L;
        Bukkit.getScheduler().runTaskTimer(plugin, this::generateAndAnnounceCode, getTimeUntilNextFiveMinutes(), codeTimeIntervals);
    }

    private long getTimeUntilNextHour() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int ticksUntilNextHour = (59 - minutes) * 1200 + (60 - seconds) * 20;
        return ticksUntilNextHour;
    }

    private long getTimeUntilNextFiveMinutes() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE) % 5;
        int seconds = calendar.get(Calendar.SECOND);
        int ticksUntilNextFiveMinutes = (4 - minutes) * 1200 + (60 - seconds) * 20;
        return ticksUntilNextFiveMinutes;
    }


    private void generateAndAnnounceCode() {
        isCodeActive = true;
        FileConfiguration config = plugin.getConfig();
        int codeLength = config.getInt("code-length");
        currentCode = RandomStringUtils.randomAlphanumeric(codeLength);


        String message = config.getString("code-message").replace("<kod>", currentCode);
        Bukkit.broadcastMessage(message);
    }

    public void playerEnteredCode(Player player, String code) {
        if (!isCodeActive) return;
        if (code.equals(currentCode)) {
            isCodeActive = false;
            String command = plugin.getConfig().getString("reward-command").replace("<player>", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

            String winnerMessage = plugin.getConfig().getString("winner-message").replace("<nick>", player.getName());
            Bukkit.broadcastMessage(winnerMessage);
        }
    }

    public boolean isCodeActive() {
        return isCodeActive;
    }

    public String getCurrentCode() {
        return currentCode;
    }
}
