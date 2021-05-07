package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.MagicWeaponItem;
import io.github.mrriptide.peakcraft.runnables.UpdatePlayer;
import io.github.mrriptide.peakcraft.util.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Bukkit.getScheduler().runTaskLater(PeakCraft.instance, () -> {
            TablistManager.removePlayers(e.getPlayer());

            TablistManager.fillBoard(e.getPlayer());
        }, 10);

        BukkitTask task = new UpdatePlayer(e.getPlayer()).runTaskTimer(PeakCraft.instance, 0, 10);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent e){
        if (e.getItem() != null){
            Item item = new Item(e.getItem());

            if (item instanceof MagicWeaponItem){

            }
        }
    }
}
