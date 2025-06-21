package me.grownbowl.championsarena.arena;

import me.grownbowl.championsarena.Plugin;
import me.grownbowl.championsarena.stats.TopStats;
import me.grownbowl.championsarena.util.ChatUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    private final static List<Arena> arenas = new ArrayList<>();

    public static Arena getArenaBy(String name) {
        for (Arena arena: arenas) {
            if (arena.name.equals(name)) {
                return arena;
            }
        }

        return null;
    }

    public static Arena getArenaBy(Player player) {
        for (Arena arena: arenas) {
            if (arena.firstPlayer == player || arena.secondPlayer == player) {
                return arena;
            }
        }

        return null;
    }

    public static Arena getFreeArena() {
        for (Arena arena: arenas) {
            if (arena.arenaStatus == ArenaStatus.OPEN) {
                return arena;
            }
        }

        return null;
    }

    public static void saveArenas() {
        File folder = new File(Plugin.getInstance().getDataFolder().getAbsolutePath() + "/arenas");

        folder.mkdirs();

        for (Arena arena: arenas) {
            File file = new File(folder.getAbsolutePath() + "/" + arena.getName() + ".yml");

            try {
                file.createNewFile();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            YamlConfiguration config = new YamlConfiguration();

            config.set("max_rounds", arena.maxRounds);
            config.set("first_spawn", arena.firstSpawn);
            config.set("second_spawn", arena.secondSpawn);

            try {
                config.save(file);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void loadArenas() {
        File folder = new File(Plugin.getInstance().getDataFolder().getAbsolutePath() + "/arenas");

        if (folder.exists()) {
            for (File file: folder.listFiles()) {
                Arena arena = new Arena(file.getName().replace(".yml", ""));

                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                arena.maxRounds = config.getInt("max_rounds");
                arena.firstSpawn = config.getLocation("first_spawn");
                arena.secondSpawn = config.getLocation("second_spawn");

                arena.openArena();
            }
        }
    }

    private final String name;

    private Location firstSpawn = null;
    private Location secondSpawn = null;

    private Player firstPlayer = null;
    private Player secondPlayer = null;

    private ArenaStatus arenaStatus = ArenaStatus.CLOSED;

    private int firstPlayerScore = 0;
    private int secondPlayerScore = 0;

    private int maxRounds = 4;

    private int currentRoundNum = 0;

    private Round currentRound = null;

    public Arena(String name) {
        this.name = name;

        arenas.add(this);
    }

    public void reset() {
        firstPlayerScore = 0;
        secondPlayerScore = 0;

        firstPlayer.teleport(firstPlayer.getWorld().getSpawnLocation());
        secondPlayer.teleport(secondPlayer.getWorld().getSpawnLocation());

        clearPlayer(firstPlayer);
        clearPlayer(secondPlayer);

        firstPlayer = null;
        secondPlayer = null;

        currentRoundNum = 0;

        arenaStatus = ArenaStatus.OPEN;

    }

    private void clearPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);

        player.getInventory().clear();

        for (PotionEffect potionEffect: player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
    }

    public void startGame(Player player1, Player player2) {
        firstPlayer = player1;
        secondPlayer = player2;

        ChatUtil.sendTitle(player1, Plugin.getInstance().getConfig().getString("messages.game_started"), "");
        ChatUtil.sendTitle(player2, Plugin.getInstance().getConfig().getString("messages.game_started"), "");

        arenaStatus = ArenaStatus.GAME_GOING;
        startNewRound();

    }

    public void onRoundEnd(Winner winner) {
        if (winner == Winner.FIRST) {
            ChatUtil.sendTitle(firstPlayer, Plugin.getInstance().getConfig().getString("messages.winner"), "");
            firstPlayerScore += 1;

            ChatUtil.sendTitle(secondPlayer, Plugin.getInstance().getConfig().getString("messages.looser"), "");

        } else if (winner == Winner.SECOND) {
            ChatUtil.sendTitle(secondPlayer, Plugin.getInstance().getConfig().getString("messages.winner"), "");
            secondPlayerScore += 1;

            ChatUtil.sendTitle(firstPlayer, Plugin.getInstance().getConfig().getString("messages.looser"), "");

        } else {
            ChatUtil.sendTitle(firstPlayer, Plugin.getInstance().getConfig().getString("messages.draw"), "");
            ChatUtil.sendTitle(secondPlayer, Plugin.getInstance().getConfig().getString("messages.draw"), "");
        }

        ChatUtil.sendMessage(firstPlayer, "&c" + firstPlayerScore + ":" + secondPlayerScore);
        ChatUtil.sendMessage(secondPlayer, "&c" + secondPlayerScore + ":" + firstPlayerScore);

        currentRound.getTimer().cancel();
        currentRound.getBossBar().removeAll();

        new BukkitRunnable(){

            @Override
            public void run() {
                if (currentRoundNum < maxRounds) {
                    startNewRound();
                } else {
                    if (firstPlayerScore > secondPlayerScore) {
                        endGame(Winner.FIRST);

                    } else if (secondPlayerScore > firstPlayerScore) {
                        endGame(Winner.SECOND);

                    } else {
                        endGame(Winner.TIE);
                    }
                }

            }
        }.runTaskLater(Plugin.getInstance(), 100L);
    }

    public void endGame(Winner winner) {
        if (winner == Winner.FIRST) {
            ChatUtil.sendTitle(firstPlayer, Plugin.getInstance().getConfig().getString("messages.game_winner"), "");
            ChatUtil.sendTitle(secondPlayer, Plugin.getInstance().getConfig().getString("messages.game_looser"), "");

            TopStats.instance.addWin(firstPlayer);

        } else if (winner == Winner.SECOND) {
            ChatUtil.sendTitle(secondPlayer, Plugin.getInstance().getConfig().getString("messages.game_winner"), "");
            ChatUtil.sendTitle(firstPlayer, Plugin.getInstance().getConfig().getString("messages.game_looser"), "");

            TopStats.instance.addWin(secondPlayer);

        } else {
            ChatUtil.sendTitle(firstPlayer, Plugin.getInstance().getConfig().getString("messages.draw"), "");
            ChatUtil.sendTitle(secondPlayer, Plugin.getInstance().getConfig().getString("messages.draw"), "");
        }

        arenaStatus = ArenaStatus.CLOSED;

        reset();
    }

    private void startNewRound() {
        currentRound = new Round(this);
        currentRoundNum += 1;
    }


    public boolean openArena() {
        if (firstSpawn != null && secondSpawn != null) {
            arenaStatus = ArenaStatus.OPEN;
            return true;
        }

        return false;
    }

    public void setFirstSpawn(Location firstSpawn) {
        this.firstSpawn = firstSpawn;
    }

    public void setSecondSpawn(Location secondSpawn) {
        this.secondSpawn = secondSpawn;
    }


    public Location getFirstSpawn() {
        return firstSpawn;
    }

    public Location getSecondSpawn() {
        return secondSpawn;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public String getName() {
        return name;
    }
}
