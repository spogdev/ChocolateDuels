package live.spog.chocolateDuels;

import live.spog.chocolateDuels.commands.KitCommand;
import live.spog.chocolateDuels.data.PlayerCache;
import live.spog.chocolateDuels.kits.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;

/*
notes:
- store items in the data files by index in the player inventory not in order of loaded into the menu. This way the size of a kit can be smaller when stored in kits.yml
increasing loading and saving speeds.
- cache kit layouts so they can be retrieved quicker if the player has not disconnected. only store them when the player disconnects.
- provide an option in config if kit layouts should be stored temporarily in ram then stored in disk upon disconnection. or store everything in disk straight away
- database support would be nice
*/

public final class DuelsPlugin extends JavaPlugin {

    private static DuelsPlugin plugin;
    private static PlayerCache playerCache;

    private File configFile;
    private File kitsFile;

    private YamlConfiguration config;
    private YamlConfiguration kits;

    @Override
    public void onEnable() {
        plugin = this;
        playerCache = new PlayerCache();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        configFile = new File(getDataFolder(), "config.yml");

        // Load kits.yml (no migration needed)
        kitsFile = new File(getDataFolder(), "kits.yml");
        if (!kitsFile.exists()) saveResource("kits.yml", false);
        kits = YamlConfiguration.loadConfiguration(kitsFile);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        saveDataFiles();
    }

    public boolean saveDataFiles() {
        try {
            kits.save(kitsFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static DuelsPlugin getInstance() { return plugin; }
    public static PlayerCache getPlayerCache() { return playerCache; }

    public YamlConfiguration getKits() { return kits; }
    public YamlConfiguration getConf() { return config; }

    public void registerCommands() {
        getCommand("kit").setExecutor(new KitCommand());
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new KitManager(), plugin);
    }
}

