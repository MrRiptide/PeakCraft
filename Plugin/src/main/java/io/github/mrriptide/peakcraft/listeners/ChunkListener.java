package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.BruteEntity;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;

public class ChunkListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        for (int i = 0; i < event.getChunk().getEntities().length; i++){
            Entity entity = event.getChunk().getEntities()[i];
            if (((CraftCreature)entity).getHandle() instanceof io.github.mrriptide.peakcraft.entity.Entity){
                PeakCraft.getPlugin().getLogger().info("somehow loaded a registered entity from a new chunk");
            } else {
                String id = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "id", "null");
                switch (id){
                    case "null":
                        ((CraftCreature)entity).setHandle(null);
                        break;
                    case "brute":
                        ((CraftCreature)entity).setHandle(new BruteEntity(entity.getLocation()));
                        break;

                }
            }
        }
    }
}
