package me.xlucash.xlrandomcode.managers;

import me.xlucash.xlrandomcode.XLRandomCode;
import me.xlucash.xlrandomcode.models.Reward;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CodeManager {
    private final XLRandomCode plugin;
    private FileConfiguration config;
    private final Random random = new Random();
    private final List<Reward> rewards;

    private String currentCode;
    private boolean isCodeActive = false;
    private Reward currentReward;

    public CodeManager(XLRandomCode plugin) {
        this.plugin = plugin;
        loadConfig();
        this.rewards = fetchRewards();
        scheduleNextCode();
    }

    private void loadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public void scheduleNextCode() {
        long codeTimeIntervals = config.getInt("time-delay-in-minutes") * 60 * 20L;
        Bukkit.getScheduler().runTaskTimer(plugin, this::generateAndAnnounceCode, getTimeUntilNextHour(), codeTimeIntervals);
    }

    private long getTimeUntilNextHour() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int ticksUntilNextHour = (59 - minutes) * 1200 + (60 - seconds) * 20;
        return ticksUntilNextHour;
    }

    private void generateAndAnnounceCode() {
        isCodeActive = true;
        int codeLength = config.getInt("code-length");
        currentCode = RandomStringUtils.randomAlphanumeric(codeLength);

        if(!rewards.isEmpty()) {
            currentReward = selectRandomReward();

            String message = config.getString("code-message").replace("<kod>", getCurrentCode()).replace("<Nagroda>", currentReward.getDisplayName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            Bukkit.getLogger().warning("[XL-RandomCode] No rewards available in the configuration!");
        }
    }

    public void playerEnteredCode(Player player, String code) {
        if (!isCodeActive) return;

        if (!code.equals(currentCode)) {
            String wrongCodeMessage = plugin.getConfig().getString("wrong-code-message");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', wrongCodeMessage));
            return;
        }

        isCodeActive = false;

        String rewardCommand = currentReward.getCommand().replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);

        String winnerMessage = plugin.getConfig().getString("winner-message").replace("<nick>", player.getName());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winnerMessage));
    }

    private List<Reward> fetchRewards() {
        List<Map<?, ?>> rewardsList = config.getMapList("rewards");
        if (rewardsList == null) return Collections.emptyList();

        return rewardsList.stream()
                .map(map -> new Reward((String) map.get("displayname"), (String) map.get("command")))
                .collect(Collectors.toList());
    }

    public boolean isCodeActive() {
        return isCodeActive;
    }

    public String getCurrentCode() {
        return currentCode;
    }

    private Reward selectRandomReward() {
        if (rewards.isEmpty()) {
            return null;
        }
        return rewards.get(random.nextInt(rewards.size()));
    }
}
