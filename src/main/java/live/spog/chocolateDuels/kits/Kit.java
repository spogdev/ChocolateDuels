package live.spog.chocolateDuels.kits;

import live.spog.chocolateDuels.DuelsPlugin;
import live.spog.chocolateDuels.utils.ItemSer;
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
            serializedItems.add(ItemSer.serializeItem(item == null ? air : item));
        }

        List<Map<String, Object>> serializedArmor = new ArrayList<>();
        for (ItemStack item : this.armor) {
            serializedArmor.add(ItemSer.serializeItem(item == null ? air : item));
        }

        if (offHand == null) offHand = air;

        return Map.of(
                "items", serializedItems,
                "armor", serializedArmor,
                "offHand", ItemSer.serializeItem(offHand)
        );
    }


    public static Kit deserialize(Map<String, Object> serialized) {
        List<ItemStack> deserializedItems = new ArrayList<>();

        Object rawItems = serialized.get("items");
        if (rawItems instanceof MemorySection section) {
            for (String key : section.getKeys(false)) {
                var cs = section.getConfigurationSection(key);
                if (cs == null) {
                    deserializedItems.add(new ItemStack(Material.AIR));
                    continue;
                }
                Map<String, Object> data = cs.getValues(false); // IMPORTANT
                deserializedItems.add(ItemSer.deserializeItem(data));
            }
        } else if (rawItems instanceof List<?> list) {
            for (Object o : list) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) o;
                deserializedItems.add(ItemSer.deserializeItem(data));
            }
        }

        List<ItemStack> deserializedArmor = new ArrayList<>();
        Object rawArmor = serialized.get("armor");
        if (rawArmor instanceof MemorySection section) {
            for (String key : section.getKeys(false)) {
                var cs = section.getConfigurationSection(key);
                if (cs == null) {
                    deserializedArmor.add(new ItemStack(Material.AIR));
                    continue;
                }
                Map<String, Object> data = cs.getValues(false); // IMPORTANT
                deserializedArmor.add(ItemSer.deserializeItem(data));
            }
        } else if (rawArmor instanceof List<?> list) {
            for (Object o : list) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) o;
                deserializedArmor.add(ItemSer.deserializeItem(data));
            }
        }

        Object rawOffhand = serialized.get("offHand");
        Map<String, Object> offhandMap;
        if (rawOffhand instanceof MemorySection section) {
            offhandMap = section.getValues(false); // IMPORTANT
        } else {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) rawOffhand;
            offhandMap = data;
        }

        ItemStack offhand = ItemSer.deserializeItem(offhandMap);

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
        plugin.reloadKits();
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
        plugin.reloadKits();
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