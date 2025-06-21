package me.grownbowl.championsarena.events;

import me.grownbowl.championsarena.arena.Arena;
import me.grownbowl.championsarena.arena.Winner;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArenaEvents implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Arena arena = Arena.getArenaBy(event.getPlayer());

        if (arena != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Arena arena = Arena.getArenaBy(event.getPlayer());

        if (arena != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Arena arena = Arena.getArenaBy(event.getPlayer());

        if (arena != null) {
            if (arena.getFirstPlayer() == event.getPlayer()) {
                arena.endGame(Winner.SECOND);
            } else {
                arena.endGame(Winner.FIRST);
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.getHealth() <= event.getDamage()) {
                Arena arena = Arena.getArenaBy(player);

                if (arena != null) {
                    event.setCancelled(true);
                    player.setGameMode(GameMode.SPECTATOR);

                    if (arena.getFirstPlayer() == player) {
                        arena.onRoundEnd(Winner.SECOND);

                    } else {
                        arena.onRoundEnd(Winner.FIRST);
                    }
                }
            }
        }
    }
}
