package live.spog.chocolateDuels.kits;

import live.spog.chocolateDuels.DuelsPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Kit implements ConfigurationSerializable {
    private String name;
    private ItemStack[] items;
    private ItemStack[] armor;
    private ItemStack offHand;

    Kit(String name, ItemStack[] items, ItemStack[] armor, ItemStack offHand) {
        this.name = name;
        this.items = items;
        this.armor = armor;
        this.offHand = offHand;
    }

    Kit(ItemStack[] items, ItemStack[] armor, ItemStack offHand) {
        this.name = null;
        this.items = items;
        this.armor = armor;
        this.offHand = offHand;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public void setOffHand(ItemStack offHand) {
        this.offHand = offHand;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        List<Map<String, Object>> serializedItems = new ArrayList<>();
        ItemStack air = new ItemStack(Material.AIR);
        for (ItemStack item : this.items) {
            if (item == null) {
                serializedItems.add(air.serialize());
                continue;
            }
            serializedItems.add(item.serialize());
        }
        List<Map<String, Object>> serializedArmor = new ArrayList<>();
        for (ItemStack item : this.armor) {
            if (item == null) {
                serializedArmor.add(air.serialize());
                continue;
            }
            serializedArmor.add(item.serialize());
        }
        if (offHand == null) {
            offHand = air;
        }
        return Map.of("items", serializedItems, "armor", serializedArmor, "offHand", offHand.serialize());
    }

    public static Kit deserialize(Map<String, Object> serialized) {

        List<ItemStack> deserializedItems = new ArrayList<>();

        Object rawItems = serialized.get("items");
        if (rawItems instanceof MemorySection section) {
            for (String key : section.getKeys(false)) {
                Map<String, Object> data = section.getConfigurationSection(key).getValues(true);
                deserializedItems.add(ItemStack.deserialize(data));
            }
        } else if (rawItems instanceof List<?> list) {
            for (Object o : list) {
                deserializedItems.add(ItemStack.deserialize((Map<String, Object>) o));
            }
        }


        List<ItemStack> deserializedArmor = new ArrayList<>();

        Object rawArmor = serialized.get("armor");
        if (rawArmor instanceof MemorySection section) {
            for (String key : section.getKeys(false)) {
                Map<String, Object> data = section.getConfigurationSection(key).getValues(true);
                deserializedArmor.add(ItemStack.deserialize(data));
            }
        } else if (rawArmor instanceof List<?> list) {
            for (Object o : list) {
                deserializedArmor.add(ItemStack.deserialize((Map<String, Object>) o));
            }
        }


        Map<String, Object> offhandMap;
        Object rawOffhand = serialized.get("offHand");

        if (rawOffhand instanceof MemorySection section) {
            offhandMap = section.getValues(true);
        } else {
            offhandMap = (Map<String, Object>) rawOffhand;
        }

        ItemStack offhand = ItemStack.deserialize(offhandMap);


        return new Kit(
                deserializedItems.toArray(new ItemStack[0]),
                deserializedArmor.toArray(new ItemStack[0]),
                offhand
        );
    }


    public boolean save() {
        DuelsPlugin plugin = DuelsPlugin.getInstance();
        YamlConfiguration kits = plugin.getKits();

        if (kits == null) {
            return false;
        }

        kits.set(this.name, this.serialize());
        plugin.saveDataFiles();
        return true;
    }

    public boolean exists() {
        DuelsPlugin plugin = DuelsPlugin.getInstance();
        YamlConfiguration kits = plugin.getKits();
        if (this.name == null) {
            return false;
        }

        return kits.contains(this.name);
    }

    public static boolean exists(String name) {
        DuelsPlugin plugin = DuelsPlugin.getInstance();
        YamlConfiguration kits = plugin.getKits();
        if (kits == null) {
            return false;
        }

        if (name == null || name.isEmpty()) {
            return false;
        }

        return kits.contains(name);
    }

    public static void delete(String name) {
        DuelsPlugin plugin = DuelsPlugin.getInstance();
        YamlConfiguration kits = plugin.getKits();
        if (kits == null) {
            return;
        }

        if (!Kit.exists(name)) {
            return;
        }

        kits.set(name, null);
        plugin.saveDataFiles();
    }

    public static Kit getKit(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        DuelsPlugin plugin = DuelsPlugin.getInstance();
        YamlConfiguration kits = plugin.getKits();

        ConfigurationSection section = kits.getConfigurationSection(name);
        if (section == null) {
            return null;
        }

        Map<String, Object> serialized = section.getValues(true);

        Kit kit = Kit.deserialize(serialized);

        return new Kit(name, kit.getItems(), kit.getArmor(), kit.getOffHand());
    }

    public static List<String> list() {
        DuelsPlugin plugin = DuelsPlugin.getInstance();
        YamlConfiguration kits = plugin.getKits();

        List<String> keys = new ArrayList<>(kits.getKeys(false));
        Collections.sort(keys);
        return keys;
    }

}
