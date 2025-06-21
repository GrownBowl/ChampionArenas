package me.grownbowl.championsarena;

import me.grownbowl.championsarena.arena.Arena;
import me.grownbowl.championsarena.commands.WarpCMD;
import me.grownbowl.championsarena.events.ArenaEvents;
import me.grownbowl.championsarena.commands.MainCMD;
import me.grownbowl.championsarena.events.MatchMakerEvents;
import me.grownbowl.championsarena.stats.TopStats;
import me.grownbowl.championsarena.events.HologramEvent;
import me.grownbowl.championsarena.warp.Warp;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {

    private static Plugin instance;

    public static Plugin getInstance() {
        return instance;
    }

//    public Economy economy;
//
//    public boolean setupEconomy() {
//        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
//
//        if (registeredServiceProvider == null) {
//            return false;
//        }
//
//        economy = registeredServiceProvider.getProvider();
//
//        return true;
//    }

    @Override
    public void onEnable() {
        instance = this;

//        if (!setupEconomy()) {
//            System.out.println("Vault not found! Plugin disable");
//            getServer().getPluginManager().disablePlugin(this);
//        }

        saveDefaultConfig();

        Warp.loadWarps();

        TopStats.instance.startHologramTimer();
        Arena.loadArenas();

        getServer().getPluginManager().registerEvents(new MatchMakerEvents(), this);
        getServer().getPluginManager().registerEvents(new ArenaEvents(), this);
        getServer().getPluginManager().registerEvents(new HologramEvent(), this);

        getCommand("championsArenas").setExecutor(new MainCMD());
        getCommand("championsArenas").setTabCompleter(new MainCMD());
        getCommand("warp").setExecutor(new WarpCMD());

    }

    @Override
    public void onDisable() {
        TopStats.instance.saveFullTop();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand) {
                    entity.remove();
                }
            }
        }

        Arena.saveArenas();
    }
}
