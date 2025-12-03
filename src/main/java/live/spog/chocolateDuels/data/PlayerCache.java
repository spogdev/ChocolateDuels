package live.spog.chocolateDuels.data;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class PlayerCache {
    private final Map<Object, Map<String, Object>> cache = new HashMap<>();

    public void set(Player player, String variable, Object value) {
        cache.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(variable, value);
    }

    public Object get(Player player, String variable) {
        Map<String, Object> data = cache.get(player.getUniqueId());
        if (data == null) return null;
        return data.get(variable);
    }
}