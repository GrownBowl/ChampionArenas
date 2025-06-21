package me.grownbowl.championsarena.kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public enum ArenaKit {

    WARRIOR(
            "Воин",
            Arrays.asList(
                    new ItemStack(Material.IRON_HELMET),
                    new ItemStack(Material.IRON_CHESTPLATE),
                    new ItemStack(Material.IRON_BOOTS),
                    new ItemStack(Material.IRON_LEGGINGS),

                    new ItemStack(Material.DIAMOND_SWORD),
                    new ItemStack(Material.SHIELD)
            )
    ),

    VIKING(
            "Викингн",
            Arrays.asList(
                    new ItemStack(Material.IRON_HELMET),
                    new ItemStack(Material.IRON_CHESTPLATE),
                    new ItemStack(Material.IRON_BOOTS),
                    new ItemStack(Material.IRON_LEGGINGS),

                    new ItemStack(Material.DIAMOND_AXE),
                    new ItemStack(Material.SHIELD)
            )
    ),

    BOWMAN(
            "Лучник",
            Arrays.asList(
                    new ItemStack(Material.LEATHER_HELMET),
                    new ItemStack(Material.LEATHER_CHESTPLATE),
                    new ItemStack(Material.LEATHER_BOOTS),
                    new ItemStack(Material.LEATHER_LEGGINGS),

                    new ItemStack(Material.BOW),
                    new ItemStack(Material.ARROW, 64),
                    new ItemStack(Material.ARROW, 64),
                    new ItemStack(Material.ARROW, 64)
            )
    ),

    LOSER(
            "Лох",
            Arrays.asList(
                    new ItemStack(Material.STICK)

            )
    ),
    ;

    private final String name;
    private final List<ItemStack> items;

    public String getName() {
        return name;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    ArenaKit(String name, List<ItemStack> items) {
        this.name = name;
        this.items = items;
    }
}
