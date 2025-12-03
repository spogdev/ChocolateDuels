package live.spog.chocolateDuels.commands;

import live.spog.chocolateDuels.kits.Kit;
import live.spog.chocolateDuels.kits.KitManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KitCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create", "new":
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return false;
                }

                if (Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " already exists");
                    return true;
                }

                KitManager.kitCreator(player, args[1].toLowerCase());
                break;
            case "load":
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return false;
                }

                if (!Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " does not exist");
                    return true;
                }

                KitManager.kitLoader(player, args[1].toLowerCase());
                break;
            case "list":
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return false;
                }

                if (!Kit.list().isEmpty()) {
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "=-=-= Kits =-=-=");
                    for (String name : Kit.list()) {
                        player.sendMessage(ChatColor.GRAY + "- " + name);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "No kits exist");
                }
                break;
            case "delete":
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return false;
                }

                if (!Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " does not exist");
                    return true;
                }

                Kit.delete(args[1].toLowerCase());
                player.sendMessage(ChatColor.GREEN + "Deleted " + args[1] + " successfully");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid subcommand");
                return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return List.of("create", "load", "list", "delete");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("delete"))) {
            return Kit.list();
        }
        return List.of();
    }
}
