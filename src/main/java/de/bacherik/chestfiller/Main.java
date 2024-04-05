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
import java.util.concurrent.ThreadLocalRandom;
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

    public void fillChestsInArea(Location corner1, Location corner2, String fillType, String stackSize, boolean shuffleItems, boolean splitDoubleChests, Player sender) {
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
                        inventory.clear(); // Truhe leeren vor dem Befüllen

                        // Logik basierend auf fillType
                        if ("full".equals(fillType)) {
                            // Befüllt die Truhe komplett mit zufälligen Items
                            fillChestFully(inventory, items, stackSize, shuffleItems);
                        } else if ("single".equals(fillType)) {
                            // Fügt nur ein einzelnes Item hinzu
                            addItemToChest(inventory, items, stackSize);
                        }

                        // Wenn shuffleItems aktiv ist, werden die Items in der Truhe gemischt
                        if (shuffleItems) {
                            shuffleChestItems(inventory);
                        }

                        chest.update(); // Aktualisierung des Zustands der Truhe
                    }

                }
            }
        }

        sender.sendMessage("Alle Truhen im Bereich wurden gefüllt.");
    }

    private void fillChestFully(Inventory inventory, List<Material> items, String stackSize, boolean shuffleItems) {
        // Diese Methode füllt die Truhe komplett, basierend auf den angegebenen Optionen
        // Hier könnte eine Implementierung folgen, die die Truhe mit verschiedenen Items füllt,
        // abhängig von der gewählten stackSize-Option (full, single, random).
        switch (stackSize) {
            case "full":
                // Füllt die Truhe mit einem Item, bis sie voll ist
                while (!inventory.isEmpty()) {
                    addItemToChest(inventory, items, stackSize);
                }
                break;
            case "single":
                // Füllt die Truhe mit einem Item, bis sie voll ist, aber nur ein Item pro Slot
                for (int i = 0; i < inventory.getSize(); i++) {
                    addItemToChest(inventory, items, stackSize);
                }
                break;
            case "random":
                // Füllt die Truhe mit einem zufälligen Item, bis sie voll ist
                while (!inventory.isEmpty()) {
                    addItemToChest(inventory, items, "single");
                }
                break;
        }
    }

    private void addItemToChest(Inventory inventory, List<Material> items, String stackSize) {
        // Fügt ein einzelnes Item zur Truhe hinzu, basierend auf der stackSize-Option
        // Hier könnte die Logik stehen, die entscheidet, wie viele Einheiten des Items hinzugefügt werden (1, voller Stapel oder zufällig).
        int amount = 1;
        if (stackSize.equals("full")) {
            amount = 64;
        } else if (stackSize.equals("random")) {
            amount = ThreadLocalRandom.current().nextInt(1, 65);
        }
        inventory.addItem(new ItemStack(items.get(ThreadLocalRandom.current().nextInt(items.size())), amount));
    }

    private void shuffleChestItems(Inventory inventory) {
        // Diese Methode mischt die Items in der Truhe.
        // Es könnte eine Implementierung sein, die die Slots der Truhe zufällig tauscht.
        List<ItemStack> itemsInChest = Arrays.asList(inventory.getContents());
        Collections.shuffle(itemsInChest, ThreadLocalRandom.current());
        Iterator<ItemStack> iterator = itemsInChest.iterator();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, iterator.next());
        }
    }
}
