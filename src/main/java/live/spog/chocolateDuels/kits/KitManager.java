package live.spog.chocolateDuels.kits;

import live.spog.chocolateDuels.gui.Icons;
import live.spog.chocolateDuels.gui.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class KitManager implements Listener {
    public static void kitCreator(Player player, String name, boolean loadFromMemory) {
        ItemStack filler = Icons.FILLER.build();
        Inventory inventory = Bukkit.createInventory(player, 54, Component.text("Kit Creator"));
        int slot = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                inventory.setItem(slot, item);
            } else {
                inventory.setItem(slot, new ItemStack(Material.AIR));
            }
            slot++;
        }

        inventory.setItem(45, player.getInventory().getHelmet());
        inventory.setItem(46, player.getInventory().getChestplate());
        inventory.setItem(47, player.getInventory().getLeggings());
        inventory.setItem(48, player.getInventory().getBoots());
        inventory.setItem(49, player.getInventory().getItemInOffHand());

        Utils.setLine(inventory, 4, filler, true);
        inventory.setItem(50, filler);
        inventory.setItem(51, filler);
        inventory.setItem(52, filler);
        inventory.setItem(53, Icons.SAVE_KIT.build());

        Utils.applyIdentifier(inventory, 50, "kit_creator");
        Utils.applyArtifact(inventory, 50, "kit_name", name);

        player.openInventory(inventory);
    }

    public static void kitLoader(Player player, String name) {
        Kit kit = Kit.getKit(name);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Failed to load " + name);
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.clear();

        ItemStack air = new ItemStack(Material.AIR);

        inventory.setHelmet(air);
        inventory.setChestplate(air);
        inventory.setLeggings(air);
        inventory.setBoots(air);
        inventory.setItemInOffHand(air);

        inventory.setContents(kit.getItems());
        inventory.setHelmet(kit.getArmor()[3]);
        inventory.setChestplate(kit.getArmor()[2]);
        inventory.setLeggings(kit.getArmor()[1]);
        inventory.setBoots(kit.getArmor()[0]);
        inventory.setItemInOffHand(kit.getOffHand());

        player.sendMessage(ChatColor.GREEN + name + " has been loaded to inventory");
    }

    public static void kitViewer(Player player, String name) {
        ItemStack filler = Icons.FILLER.build();
        Inventory inventory = Bukkit.createInventory(player, 54, Component.text("Kit Viewer"));

        Kit kit = Kit.getKit(name);

        inventory.setContents(kit.getItems());

        Utils.setLine(inventory, 4, filler, true);
        inventory.setItem(50, filler);
        inventory.setItem(51, filler);
        inventory.setItem(52, filler);
        inventory.setItem(53, filler);

        Utils.applyIdentifier(inventory, 50, "kit_viewer");
        Utils.applyArtifact(inventory, 50, "kit_name", name);

        inventory.setItem(45, kit.getArmor()[3]);
        inventory.setItem(46, kit.getArmor()[2]);
        inventory.setItem(47, kit.getArmor()[1]);
        inventory.setItem(48, kit.getArmor()[0]);
        inventory.setItem(49, kit.getOffHand());

        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getView().getTopInventory();
        ItemStack item = event.getCurrentItem();
        int slot = event.getRawSlot();

        if (inventory.getSize() != 54) return;

        if (Utils.getIdentifier(inventory, 50) == null) return;

        if (Objects.equals(Utils.getIdentifier(inventory, 50), "kit_creator") || Objects.equals(Utils.getIdentifier(inventory, 50), "kit_viewer")) event.setCancelled(true);

        if (Icons.isIcon(item)) {
            // handle improper cases
            if (Icons.FILLER.equals(item) && inventory.getType() != InventoryType.CHEST) {
                event.setCurrentItem(new ItemStack(Material.AIR)); // delete item if it is outside a menu
            }

            event.setCancelled(true);
            if (Icons.SAVE_KIT.equals(item)) {
                String name = Utils.getArtifact(inventory, 50, "kit_name");

                ItemStack[] kit_items = new ItemStack[36];
                for (int i = 0; i < 36; i++) {
                    kit_items[i] = inventory.getItem(i);
                }

                ItemStack[] kit_armor = new ItemStack[4];
                kit_armor[0] = inventory.getItem(48); // helmet
                kit_armor[1] = inventory.getItem(47); // chest
                kit_armor[2] = inventory.getItem(46); // legs
                kit_armor[3] = inventory.getItem(45); // boots

                ItemStack offhand = inventory.getItem(49);

                Kit kit = new Kit(name, kit_items, kit_armor, offhand);

                if (kit.save()) {
                    player.sendMessage(ChatColor.GREEN + "Kit '" + name + "' created");
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to create kit, Please contact an admin");
                }
            } else if (Icons.EDIT_KIT.equals(item)) {
                String name = Utils.getArtifact(inventory, 50, "kit_name");
            }
        }


    }
}
