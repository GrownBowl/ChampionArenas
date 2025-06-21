package me.grownbowl.championsarena.events;

import me.grownbowl.championsarena.matchmaker.MatchMaker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MatchMakerEvents implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        MatchMaker.instance.playerInQueue.remove(event.getPlayer());
    }
}
