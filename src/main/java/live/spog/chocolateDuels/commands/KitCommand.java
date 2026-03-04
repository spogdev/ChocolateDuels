package live.spog.chocolateDuels.commands;

import live.spog.chocolateDuels.kits.Kit;
import live.spog.chocolateDuels.kits.KitManager;
import live.spog.chocolateDuels.utils.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
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
                if (!player.hasPermission("chocolateduels.create")) {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                }

                if (Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " already exists");
                    return true;
                }

                KitManager.kitCreator(player, args[1].toLowerCase(), false);
                break;
            case "load":
                if (!player.hasPermission("chocolateduels.load")) {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                }

                if (!Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " does not exist");
                    return true;
                }

                KitManager.kitLoader(player, args[1].toLowerCase());
                break;
            case "list":
                if (!player.hasPermission("chocolateduels.view")) {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                    return true;
                }

                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                }

                if (!Kit.list().isEmpty()) {
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "=-=-= Kits =-=-=");
                    for (String name : Kit.list()) {
                        TextComponent viewButton = Component.text("[View] ")
                                .color(NamedTextColor.AQUA)
                                .clickEvent(ClickEvent.runCommand("/kits view " + name))
                                .hoverEvent(HoverEvent.showText(Component.text("Click to edit '" + name + "'", NamedTextColor.AQUA)));
                        TextComponent editButton = Component.text("[Edit]")
                                .color(NamedTextColor.GOLD)
                                .clickEvent(ClickEvent.runCommand("/kits edit " + name))
                                .hoverEvent(HoverEvent.showText(Component.text("Click to view '" + name + "'", NamedTextColor.GOLD)));

                        if (!player.hasPermission("chocolateduels.edit")) {
                            editButton = Component.text("");
                        }

                        player.sendMessage(Message.of("&7- " + name + " ").asFormattedComponent().append(viewButton).append(editButton));
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "No kits exist");
                }
                break;
            case "delete":
                if (!player.hasPermission("chocolateduels.delete")) {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                }

                if (!Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " does not exist");
                    return true;
                }

                Kit.delete(args[1].toLowerCase());
                player.sendMessage(ChatColor.GREEN + "Deleted " + args[1] + " successfully");
                break;
            case "view":
                if (!player.hasPermission("chocolateduels.view")) {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                }

                if (!Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " does not exist");
                    return true;
                }

                KitManager.kitViewer(player, args[1].toLowerCase());
                break;
            case "edit":
                if (!player.hasPermission("chocolateduels.edit")) {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                }

                if (!Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " does not exist");
                    return true;
                }

                KitManager.kitEditor(player, args[1].toLowerCase());
                break;
            case "rename":
                if (!player.hasPermission("chocolateduels.edit")) {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                    return true;
                }

                if (args.length != 3) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                }

                if (!Kit.exists(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.RED + args[1] + " does not exist");
                    return true;
                }

                Kit kit = Kit.getKit(args[1].toLowerCase());
                kit.rename(args[2].toLowerCase());
                player.sendMessage(ChatColor.GREEN + "Renamed " + args[1] + " to " + args[2]);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid subcommand");
                return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return List.of("create", "load", "list", "delete", "view", "edit", "rename");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("rename") || args[0].equalsIgnoreCase("edit"))) {
            return Kit.list();
        }
        return List.of();
    }
}
