package io.github.mrriptide.peakcraft.runnables;

import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Update player thingy
 *
 * Should be called once every 10 ticks per player
 *
 * **/
public class UpdatePlayer extends BukkitRunnable {
    private UUID playerUUID;
    public UpdatePlayer(Player player){
        this.playerUUID = player.getUniqueId();
    }

    @Override
    public void run() {
        Player player = Bukkit.getServer().getPlayer(playerUUID);
        if (player != null){
            PlayerWrapper wrapper = new PlayerWrapper(player);
            wrapper.updateEntity();

            wrapper.tryNaturalRegen();
        } else {
            this.cancel();
        }
    }
}
