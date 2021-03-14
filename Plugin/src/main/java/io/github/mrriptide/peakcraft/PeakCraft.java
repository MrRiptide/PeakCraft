package io.github.mrriptide.peakcraft;

import io.github.mrriptide.peakcraft.commands.CommandGive;
import openj9.internal.tools.attach.target.Command;
import org.bukkit.plugin.java.JavaPlugin;

public class PeakCraft extends JavaPlugin {
    public static PeakCraft instance;

    public static PeakCraft getPlugin(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this; // provides an instance of the plugin to the rest of the code !!MUST BE DONE BEFORE ANYTHING ELSE!!

        // Load items from items.tsv, must be done as one of the first things
        getLogger().info("Loading items");
        ItemManager.LoadItems();

        // Register all commands
        this.getCommand("give").setExecutor(new CommandGive());

    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }

    public static void disable(){
        getPlugin().getServer().getPluginManager().disablePlugin(instance);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        LivingEntity entity = e.getEntity();
        if (entity instanceof Player){
            Player player = (Player) entity;


        }
    }
}
