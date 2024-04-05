package de.bacherik.chestfiller.cmd;

import de.bacherik.chestfiller.Main;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChestFill implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public ChestFill(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl verwenden.");
            return true;
        }

        // Validierung und Parsing der Argumente
        if (args.length != 10) { // Anpassung für die neuen Argumente
            sender.sendMessage("Ungültige Anzahl an Argumenten. Verwendung: /fillchests <x1> <y1> <z1> <x2> <y2> <z2> <fillType> <stackSize> <shuffleItems> <splitDoubleChests>");
            return true;
        }

        Player player = (Player) sender;
        try {
            Location corner1 = new Location(player.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            Location corner2 = new Location(player.getWorld(), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));

            // Verarbeitung der neuen Argumente
            String fillType = args[6];
            String stackSize = args[7];
            boolean shuffleItems = Boolean.parseBoolean(args[8]);
            boolean splitDoubleChests = Boolean.parseBoolean(args[9]);

            plugin.fillChestsInArea(corner1, corner2, fillType, stackSize, shuffleItems, splitDoubleChests, player);

            player.sendMessage("Truhen im Bereich wurden entsprechend der neuen Argumente gefüllt!");
        } catch (NumberFormatException e) {
            player.sendMessage("Ungültige Koordinaten.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Tab-Completion-Logik bleibt unverändert
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlockExact(5); // 5 ist die maximale Entfernung
            if (targetBlock != null) {
                Location loc = targetBlock.getLocation();
                List<String> completions = new ArrayList<>();
                switch (args.length) {
                    case 1:
                    case 4:
                        completions.add(String.valueOf(loc.getBlockX()));
                        break;
                    case 2:
                    case 5:
                        completions.add(String.valueOf(loc.getBlockY()));
                        break;
                    case 3:
                    case 6:
                        completions.add(String.valueOf(loc.getBlockZ()));
                        break;
                    // Hinzu kommen die neuen Argumente für die Tab-Vervollständigung
                    case 7:
                        completions.add("full");
                        completions.add("single");
                        break;
                    case 8:
                        completions.add("full");
                        completions.add("single");
                        completions.add("random");
                        break;
                    case 9:
                    case 10:
                        completions.add("true");
                        completions.add("false");
                        break;
                }
                return completions;
            }
        }
        return Collections.emptyList();
    }
}