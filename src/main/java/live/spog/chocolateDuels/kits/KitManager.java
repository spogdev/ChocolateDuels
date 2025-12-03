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

public class KitManager implements Listener {
    public static void kitCreator(Player player, String name) {
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

        Utils.applyIdentifier(inventory, 52, "kit_creator");
        Utils.applyArtifact(inventory, 52, "kit_name", name);

        player.openInventory(inventory);
    }

    public static void kitLoader(Player player, String name) {
        Kit kit = Kit.getKit(name);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Failed to load " + name);
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.setContents(kit.getItems());
        inventory.setHelmet(kit.getArmor()[0]);
        inventory.setChestplate(kit.getArmor()[1]);
        inventory.setLeggings(kit.getArmor()[2]);
        inventory.setBoots(kit.getArmor()[3]);
        inventory.setItemInOffHand(kit.getOffHand());

        player.sendMessage(ChatColor.GREEN + name + " has been loaded to inventory");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getView().getTopInventory();
        ItemStack item = event.getCurrentItem();
        int slot = event.getRawSlot();

        if (Icons.isIcon(item)) {
            // handle improper cases
            if (Icons.FILLER.equals(item) && inventory.getType() != InventoryType.CHEST) {
                event.setCurrentItem(new ItemStack(Material.AIR)); // delete item if it is outside a menu
            }

            if (Utils.getIdentifier(inventory, 52) == null) return;
            if (!Utils.getIdentifier(inventory, 52).equals("kit_creator")) return;

            event.setCancelled(true);
            if (Icons.SAVE_KIT.equals(item)) {
                String name = Utils.getArtifact(inventory, 52, "kit_name");

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
                    player.sendMessage(ChatColor.RED + "Failed to create kit, Please contact a staff member or admin");
                }
            }
        }


    }
}
