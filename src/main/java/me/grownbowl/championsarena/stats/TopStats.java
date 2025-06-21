package me.grownbowl.championsarena.stats;

import me.grownbowl.championsarena.Plugin;
import me.grownbowl.championsarena.util.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TopStats {
    public static final TopStats instance = new TopStats();

    public Map<UUID, Integer> top = loadFullTop();

    public Hologram hologram = new Hologram(
            new Location(Bukkit.getWorld(Plugin.getInstance().getConfig().getString("player_top.world")),
                    Plugin.getInstance().getConfig().getInt("player_top.x"),
                    Plugin.getInstance().getConfig().getInt("player_top.y"),
                    Plugin.getInstance().getConfig().getInt("player_top.z")),
            Arrays.asList("ТОП 10 НА АРЕНАХ"));

    public void saveFullTop() {
        File folder = new File(Plugin.getInstance().getDataFolder().getAbsolutePath() + "/fullTop");
        folder.mkdirs();

        File file = new File(folder.getAbsolutePath() + '/' + "tops.yml");

        try {
            file.createNewFile();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, Integer> entry: top.entrySet()) {
            String key = entry.getKey().toString();
            Integer value = entry.getValue();

            config.set("topStatistics." + key, value);
        }

        try {
            config.save(file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Map<UUID, Integer> loadFullTop() {
        File folder = new File(Plugin.getInstance().getDataFolder().getAbsolutePath() + "/fullTop");
        Map<UUID, Integer> map = new HashMap<>();

        if (folder.exists()) {
            File file = folder.listFiles()[0];

            if (file == null) {
                return map;
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (config.isConfigurationSection("topStatistics")) {
                for (String key: config.getConfigurationSection("topStatistics").getKeys(false)) {
                    try {
                        UUID uuid = UUID.fromString(key);
                        int value = config.getInt("topStatistics." + key);

                        System.out.println("load top:");
                        System.out.println(key);
                        System.out.println(uuid);
                        System.out.println(value);

                        map.put(uuid, value);

                    } catch (IllegalArgumentException e) {
                        // Некорректный UUID в конфиге
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("printed map from config");
        System.out.println(map);
        return map;
    }

    public void addWin(Player player) {
        if (top.containsKey(player.getUniqueId())) {
            top.put(player.getUniqueId(), top.get(player.getUniqueId()) + 1);

        } else {
            top.put(player.getUniqueId(), 1);
        }
    }

    private List<String> getTextTopTen() {
        List<UUID> currentTop = new ArrayList<>();
        List<String> text = new ArrayList<>();

        text.add(Plugin.getInstance().getConfig().getString("player_top.header"));

        UUID maxPlayer = null;
        int maxWins = 0;

        for (int i = 0; i < 10; i++) {
            for (UUID uuid : top.keySet()) {
                if (top.get(uuid) > maxWins && !currentTop.contains(uuid)) {
                    maxPlayer = uuid;
                    maxWins = top.get(uuid);
                }
            }

            if (maxPlayer != null) {
                System.out.println(maxPlayer);
                System.out.println(maxWins);

                text.add(
                        Plugin.getInstance().getConfig().getString("player_top.line")
                                .replace("%player%", Bukkit.getOfflinePlayer(maxPlayer).getName())
                                .replace("%wins%", String.valueOf(maxWins))
                );

                currentTop.add(maxPlayer);

                maxWins = 0;
                maxPlayer = null;
            }
        }

        return text;
    }

    public void startHologramTimer() {
        hologram.createSpinningBlockOnTop(Material.valueOf(Plugin.getInstance().getConfig().getString("player_top.spinning_item")));

        new BukkitRunnable() {

            @Override
            public void run() {
                hologram.setText(getTextTopTen());
            }
        }.runTaskTimer(Plugin.getInstance(), 0L, 1200L);
    }
}
