package live.spog.chocolateDuels.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public enum Icons {
    SAVE_KIT("Save Kit", Material.BOOK, List.of(), "#30f237"),
    FILLER(" ", Material.GRAY_STAINED_GLASS_PANE, List.of(), "#707070"),
    EDIT_KIT("Edit Kit", Material.WRITABLE_BOOK, List.of(), "#ede326");

    private final String name;
    private final Material material;
    private final List<String> lore;
    private final String color;

    Icons(String name, Material material, List<String> lore, String color) {
        this.name = name;
        this.material = material;
        this.lore = lore;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getColor() {
        return color;
    }

    public List<Component> getLoreAsComponentList() {
        List<Component> list = new ArrayList<>();
        if (this.getLore().isEmpty()) {
            return List.of();
        }
        for (String s : this.getLore()) {
            list.add(Component.text(s).color(TextColor.fromHexString(this.getColor())));
        }

        return list;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(this.getName()).color(TextColor.fromHexString(this.getColor())));
        meta.lore(this.getLoreAsComponentList());
        meta.getPersistentDataContainer().set(new NamespacedKey("cd", "button_type"), PersistentDataType.STRING, this.name());
        item.setItemMeta(meta);
        return item;
    }

    public boolean equals(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta.getPersistentDataContainer().has(new NamespacedKey("cd", "button_type"), PersistentDataType.STRING)) {
            Icons icon;
            try {
                icon = Icons.valueOf(meta.getPersistentDataContainer().get(new NamespacedKey("cd", "button_type"), PersistentDataType.STRING));
            } catch (Exception e) {
                return false;
            }

            return icon == this;
        }
        return false;
    }

    public static boolean isIcon(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta.getPersistentDataContainer().has(new NamespacedKey("cd", "button_type"), PersistentDataType.STRING)) {
            try {
                Icons.valueOf(meta.getPersistentDataContainer().get(new NamespacedKey("cd", "button_type"), PersistentDataType.STRING));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
