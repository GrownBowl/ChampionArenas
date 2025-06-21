package me.grownbowl.championsarena.arena;

import me.grownbowl.championsarena.Plugin;
import me.grownbowl.championsarena.kit.ArenaKit;
import me.grownbowl.championsarena.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Round {

    private BossBar bossBar = Bukkit.createBossBar(Plugin.getInstance().getConfig().getString("boss_bar.title").replace("%time%", "60"),
            BarColor.valueOf(Plugin.getInstance().getConfig().getString("boss_bar.bar_color")),
            BarStyle.valueOf(Plugin.getInstance().getConfig().getString("boss_bar.bar_style"))
    );

    private Arena arena;
    private BukkitRunnable timer;

    public Round(Arena arena) {
        this.arena = arena;

        preparePlayer(arena.getFirstPlayer());
        preparePlayer(arena.getSecondPlayer());

        arena.getFirstPlayer().teleport(arena.getFirstSpawn());
        arena.getSecondPlayer().teleport(arena.getSecondSpawn());

        startTimer();
    }

    public void startTimer() {
        bossBar.setProgress(1);
        bossBar.addPlayer(arena.getFirstPlayer());
        bossBar.addPlayer(arena.getSecondPlayer());

        timer = new BukkitRunnable() {
            int ctr = 60;

            @Override
            public void run() {
                ctr -= 1;

                bossBar.setProgress(ctr / 60.0);
                bossBar.setTitle(
                        ChatColor.translateAlternateColorCodes('&',
                                Plugin.getInstance().getConfig().getString("boss_bar.title")
                                        .replace("%time%", String.valueOf(ctr)))
                );

                if (ctr <= 0) {
                    arena.onRoundEnd(Winner.TIE);
                    cancel();
                }

            }
        };

        timer.runTaskTimer(Plugin.getInstance(), 0L, 20L);
    }

    public void preparePlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);

        player.getInventory().clear();

        for (PotionEffect potionEffect: player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        Random random = new Random();
        ArenaKit arenaKit = ArenaKit.values()[random.nextInt(ArenaKit.values().length)];

        for (ItemStack itemStack: arenaKit.getItems()) {
            player.getInventory().addItem(itemStack);
        }

        ChatUtil.sendMessage(player, "Вы получили набор " + arenaKit.getName());
    }

    public BukkitRunnable getTimer() {
        return timer;
    }

    public BossBar getBossBar() {
        return bossBar;
    }
}
