package hinadamari.wolfranger;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class WolfRanger extends JavaPlugin
{

    public HashMap<DyeColor, Double> config = new HashMap<DyeColor, Double>();
    public final static Logger log = Logger.getLogger("Minecraft");

    private File pluginFolder;
    private File configFile;

    public void onEnable()
    {

        pluginFolder = getDataFolder();
        configFile = new File(pluginFolder, "config.yml");
        createConfig();
        this.getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();

        new WolfRangerEventListener(this);
        getServer().getPluginManager().registerEvents(new WolfRangerEventListener(this), this);

        log.info("[WolfRanger] PredatoryWolf is enabled!");

    }

    public void onDisable()
    {
        this.getServer().getScheduler().cancelTasks(this);
    }

    private void createConfig() {
        if (!pluginFolder.exists()) {
            try {
                pluginFolder.mkdir();
            } catch (Exception e) {
                log.info("[WolfRanger] ERROR: " + e.getMessage());
            }
        }

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                log.info("[WolfRanger] ERROR: " + e.getMessage());
            }
        }
    }

    private void loadConfig() {

        config.clear();
        config.put(DyeColor.BLACK, Math.max(0, getConfig().getDouble("black")));
        config.put(DyeColor.BLUE, Math.max(0, getConfig().getDouble("blue")));
        config.put(DyeColor.RED, Math.max(1, getConfig().getDouble("red")));
        config.put(DyeColor.GREEN, Math.max(1, getConfig().getDouble("green")));

    }

    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("WolfRanger"))
        {
            if(args.length > 0)
            {
                if(args[0].equalsIgnoreCase("Reload"))
                {
                    if(!sender.hasPermission("wolfranger.reload"))
                    {
                        sender.sendMessage("You don't have predatorywolf.reload");
                        return true;
                    }
                    loadConfig();
                    sender.sendMessage(ChatColor.GREEN + "WolfRanger has been reloaded.");
                    return true;
                }
            }
        }
        return false;
    }
}