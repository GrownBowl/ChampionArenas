package me.grownbowl.championsarena.util;

import me.grownbowl.championsarena.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class Hologram {

    private Location location;
    private List<String> text;
    private List<ArmorStand> lines = new ArrayList<>();

    public Hologram(Location location, List<String> text) {
        this.location = location;
        this.text = text;

        location.setY(location.getY() - 2);
        spawnAll();
    }

    public void spawnAll() {
        Location currentLocation = location.clone();

        for (String line: text) {
            spawn(currentLocation, line);

            currentLocation.setY(currentLocation.getY() - 0.3);
        }
    }

    private void spawn(Location location, String text) {
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true); // чтобы нельзя было уничтожить
        armorStand.setMarker(true);       // уменьшает хитбокс, делает невидимым для кликов
        armorStand.setSilent(true);
        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
        armorStand.setCustomNameVisible(true);

        lines.add(armorStand);
    }

    public void setText(List<String> newText) {
        Location currentLocation = location.clone();

        for (int i = 0; i < newText.size(); i++) {
            if (lines.size() <= i) {
                spawn(currentLocation, newText.get(i));

            } else {
                lines.get(i).setCustomName(ChatColor.translateAlternateColorCodes('&', newText.get(i)));
            }

            currentLocation.setY(currentLocation.getY() - 0.3);
        }

        if (newText.size() < text.size()) {
            for (int i = newText.size(); i <= text.size(); i++) {
                lines.get(i).remove();
            }
        }

        text = newText;
    }

    public void createSpinningBlockOnTop(Material material) {
        Location headLocation = location.clone();
        headLocation.setY(headLocation.getY() + 0.3);
        ArmorStand armorStand = headLocation.getWorld().spawn(headLocation, ArmorStand.class);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.getEquipment().setHelmet(new ItemStack(material));
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setSilent(true);

        new BukkitRunnable() {

            @Override
            public void run() {
                armorStand.setRotation(armorStand.getLocation().getYaw() + 1f, 0);
            }
        }.runTaskTimer(Plugin.getInstance(), 0L, 1L);
    }
}
