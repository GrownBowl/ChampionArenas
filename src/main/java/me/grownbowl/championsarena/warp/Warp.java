package me.grownbowl.championsarena.warp;

import me.grownbowl.championsarena.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Warp {
    public static List<Warp> warps = new ArrayList<>();

    public static void loadWarps() {
        FileConfiguration config = Plugin.getInstance().getConfig();

        System.out.println(config);
        System.out.println(config.getKeys(true));

        for (String key: config.getKeys(true)) {
            if (key.startsWith("warps") && key.contains(".") && key.indexOf(".") == key.lastIndexOf(".")) {
                System.out.println(key.indexOf("."));
                System.out.println(key);
                System.out.println(key.substring(key.indexOf(".")));

                String warpName = config.getString(key + ".name");
                World world = Bukkit.getWorld(config.getString(key + ".location.world"));

                int x = config.getInt(key + ".location.x");
                int y = config.getInt(key + ".location.y");
                int z = config.getInt(key + ".location.z");

                Location warpLocation = new Location(world, x, y, z);

                Warp warp = new Warp(key.substring(key.indexOf(".") + 1), warpName, warpLocation);
                warps.add(warp);
            }
        }
    }

    private String warpId;
    private String warpName;
    private Location location;

    public Warp(String warpId, String warpName, Location location) {
        this.warpId = warpId;
        this.warpName = warpName;
        this.location = location;
    }

    public String getWarpId() {
        return warpId;
    }

    public String getWarpName() {
        return warpName;
    }

    public Location getLocation() {
        return location;
    }

    public void setWarpId(String warpId) {
        this.warpId = warpId;
    }

    public void setWarpName(String warpName) {
        this.warpName = warpName;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
