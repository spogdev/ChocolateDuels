package live.spog.chocolateDuels.utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ItemSer {
    private ItemSer() {}

    // Custom key so you can preserve repair cost even though we strip the component.
    private static final String CD_REPAIR_COST_KEY = "cd_repairCost";

    public static Map<String, Object> serializeItem(ItemStack item) {
        Map<String, Object> map = new LinkedHashMap<>(item.serialize());

        // Preserve repair cost (optional)
        Integer repairCost = getRepairCost(item);
        if (repairCost != null && repairCost > 0) {
            map.put(CD_REPAIR_COST_KEY, repairCost);
        }

        // Strip known-bad keys so Paper's ItemStack.deserialize won't explode
        stripRepairCostComponent(map);

        return map;
    }

    public static ItemStack deserializeItem(Map<String, Object> raw) {
        Map<String, Object> map = new LinkedHashMap<>(raw);

        Integer repairCost = takeInt(map.remove(CD_REPAIR_COST_KEY));

        // If old/bad saves exist, also remove flattened key form
        Integer fromFlat = takeInt(map.remove("components.minecraft:repair_cost"));
        if (repairCost == null) repairCost = fromFlat;

        // Remove from components map (nested form)
        Integer fromNested = stripRepairCostComponent(map);
        if (repairCost == null) repairCost = fromNested;

        ItemStack item = ItemStack.deserialize(map);

        // Re-apply repair cost via meta so you keep behavior without using the component map key
        if (repairCost != null && repairCost > 0) {
            final int rc = repairCost;
            item.editMeta(Repairable.class, m -> m.setRepairCost(rc));
        }

        return item;
    }

    private static Integer getRepairCost(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Repairable r) {
            return r.getRepairCost();
        }
        return null;
    }

    /**
     * Removes components.minecraft:repair_cost (flattened) and components->minecraft:repair_cost (nested).
     * Returns the removed nested value if present.
     */
    @SuppressWarnings("unchecked")
    private static Integer stripRepairCostComponent(Map<String, Object> map) {
        // Flattened form (defensive)
        Integer flat = takeInt(map.remove("components.minecraft:repair_cost"));

        Object compsObj = map.get("components");
        if (!(compsObj instanceof Map<?, ?> rawComps)) {
            return flat;
        }

        Map<String, Object> comps = new LinkedHashMap<>((Map<String, Object>) rawComps);
        Integer nested = takeInt(comps.remove("minecraft:repair_cost"));

        if (comps.isEmpty()) map.remove("components");
        else map.put("components", comps);

        return nested != null ? nested : flat;
    }

    private static Integer takeInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        if (o instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
