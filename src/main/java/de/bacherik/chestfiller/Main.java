package de.bacherik.chestfiller;

import de.bacherik.chestfiller.cmd.ChestFill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("fillchests").setExecutor(new ChestFill(this));
        this.getCommand("fillchests").setTabCompleter(new ChestFill(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void fillChestsInArea(Location corner1, Location corner2, Player sender) {
        World world = corner1.getWorld();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        List<Material> items = Arrays.stream(Material.values())
                .filter(material -> material.isItem() && !material.equals(Material.AIR)) // Filterung ungeeigneter Materialien
                .collect(Collectors.toList());
        Collections.shuffle(items);
        Iterator<Material> itemIterator = items.iterator();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getState() instanceof Chest) {
                        Chest chest = (Chest) block.getState();
                        Inventory inventory = chest.getInventory();
                        inventory.clear();

                        Material itemMaterial;
                        if (itemIterator.hasNext()) {
                            itemMaterial = itemIterator.next();
                        } else {
                            // Neu starten des Iterators, wenn das Ende erreicht ist
                            itemIterator = items.iterator();
                            itemMaterial = itemIterator.next();
                        }

                        inventory.addItem(new ItemStack(itemMaterial));
                        chest.update(); // Aktualisierung des Zustands der Truhe
                    }
                }
            }
        }

        sender.sendMessage("Alle Truhen im Bereich wurden gefüllt.");
    }
}
