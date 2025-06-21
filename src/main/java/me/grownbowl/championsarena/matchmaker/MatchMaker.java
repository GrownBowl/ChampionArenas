package me.grownbowl.championsarena.matchmaker;

import me.grownbowl.championsarena.Plugin;
import me.grownbowl.championsarena.arena.Arena;
import me.grownbowl.championsarena.util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MatchMaker {

    public static final MatchMaker instance = new MatchMaker();

    public final List<Player> playerInQueue = new ArrayList<>();

    public void joinQueue(Player player) {
        playerInQueue.add(player);

        if (playerInQueue.size() >= 2) {
            Arena arena = Arena.getFreeArena();

            Player player1 = playerInQueue.get(0);
            Player player2 = playerInQueue.get(1);
            playerInQueue.remove(player1);
            playerInQueue.remove(player2);

            if (arena != null) {

                arena.startGame(player1, player2);

            } else {

                startArenaSearch(player1, player2);
            }
        }

        ChatUtil.sendMessage(player, "Вы присоединились к очереди поиска.");
    }

    public void leaveQueue(Player player) {
        playerInQueue.remove(player);
        ChatUtil.sendMessage(player, "Вы покинулит очередь поиска.");
    }

    private void startArenaSearch(Player player1, Player player2) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!player1.isOnline()) {
                    cancel();

                    if (player2.isOnline()) {
                        joinQueue(player2);
                    }

                    return;
                }

                if (!player2.isOnline()) {
                    cancel();

                    if (player1.isOnline()) {
                        joinQueue(player1);
                    }

                    return;
                }

                Arena arena = Arena.getFreeArena();

                if (arena != null) {
                    arena.startGame(player1, player2);
                    cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0L, 20L);
    }
}
