package de.bacherik.chestfiller.cmd;

import de.bacherik.chestfiller.Main;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

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

        if (args.length != 6) {
            sender.sendMessage("Ungültige Anzahl an Argumenten. Verwendung: /fillchests <x1> <y1> <z1> <x2> <y2> <z2>");
            return true;
        }

        Player player = (Player) sender;
        try {
            Location corner1 = new Location(player.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            Location corner2 = new Location(player.getWorld(), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));

            plugin.fillChestsInArea(corner1, corner2, ((Player) sender).getPlayer());
            player.sendMessage("Truhen im Bereich wurden gefüllt!");
        } catch (NumberFormatException e) {
            player.sendMessage("Ungültige Koordinaten.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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
                }
                return completions;
            }
        }
        return Collections.emptyList();
    }
}
