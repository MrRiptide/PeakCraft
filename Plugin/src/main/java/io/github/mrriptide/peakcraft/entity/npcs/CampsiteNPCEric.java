package io.github.mrriptide.peakcraft.entity.npcs;

import io.github.mrriptide.peakcraft.util.CustomColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class CampsiteNPCEric extends NPC {
    public CampsiteNPCEric(Level world) {
        super("npc_campsite_eric", EntityType.VILLAGER, world);
        setName(CustomColors.NPC +"Eric");
        this.strength = 50;
    }

    @Override
    public void initPathfinder() {

    }
}
