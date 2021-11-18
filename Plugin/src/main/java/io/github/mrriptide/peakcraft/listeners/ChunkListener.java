package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;

public class ChunkListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        /*for (int i = 0; i < event.getChunk().getEntities().length; i++){
            Entity entity = event.getChunk().getEntities()[i];
            if (entity instanceof Creature){
                Sheep test = (Sheep)event.getChunk().getWorld().spawnEntity(event.getChunk().getWorld().getSpawnLocation(), EntityType.SHEEP);
                CraftCreature test2 = (CraftCreature)test;
                ((CraftCreature) test).getHandle();
                if (((CraftCreature)entity).getHandle() instanceof io.github.mrriptide.peakcraft.entity.LivingEntity){
                    PeakCraft.getPlugin().getLogger().info("somehow loaded a registered entity from a new chunk");
                } else {
                    String id = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "id", "null");
                    try {
                        io.github.mrriptide.peakcraft.entity.LivingEntity newEntity = EntityManager.getEntity(id, entity.getLocation());
                        ((CraftCreature)entity).setHandle(newEntity);
                    } catch (EntityException e) {
                        e.printStackTrace();
                        PeakCraft.getPlugin().getLogger().warning("Invalid entity with id \"" + id + "\" loaded");
                        entity.setCustomName(CustomColors.ERROR + "Invalid Entity");
                        entity.setGlowing(true);
                    }
                }
            }
        }*/
    }
}
