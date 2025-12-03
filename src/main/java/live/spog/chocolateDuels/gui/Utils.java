package live.spog.chocolateDuels.gui;

import live.spog.chocolateDuels.exceptions.IllegalRowException;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;

public class Utils {
    public static boolean setLine(Inventory inv, int line, ItemStack set, boolean force) throws IllegalRowException {
        if (inv.getSize() <= 9) {
            throw new IllegalRowException("Inventory size must be greater than 9");
        }

        if (line > inv.getSize() / 9) {
            throw new IllegalRowException("Line index is out of bounds");
        }

        int firstIndex = line * 9;
        for (int i = firstIndex; i < firstIndex + 9; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && !force) continue;
            inv.setItem(i, set);
        }
        return true;
    }

    public static void applyIdentifier(Inventory inv, int slot, String identifier) {
        ItemStack item = inv.getItem(slot);
        if (item == null) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(new NamespacedKey("cd", "menu_marker"), PersistentDataType.STRING, identifier);

        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public static void applyArtifact(Inventory inv, int slot, String identifier, String artifact) {
        ItemStack item = inv.getItem(slot);
        if (item == null) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(new NamespacedKey("cdartifacts", identifier), PersistentDataType.STRING, artifact);

        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public static @Nullable String getIdentifier(Inventory inv, int slot) {
        ItemStack item = inv.getItem(slot);
        if (item == null) {
            return null;
        }
        if (!item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();

        if (!meta.getPersistentDataContainer().has(new NamespacedKey("cd", "menu_marker"), PersistentDataType.STRING)) {
            return null;
        }

        return meta.getPersistentDataContainer().get(new NamespacedKey("cd", "menu_marker"), PersistentDataType.STRING);
    }

    public static @Nullable String getArtifact(Inventory inv, int slot, String identifier) {
        ItemStack item = inv.getItem(slot);
        if (item == null) {
            return null;
        }
        if (!item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();

        if (!meta.getPersistentDataContainer().has(new NamespacedKey("cdartifacts", identifier), PersistentDataType.STRING)) {
            return null;
        }

        return meta.getPersistentDataContainer().get(new NamespacedKey("cdartifacts", identifier), PersistentDataType.STRING);
    }
}
