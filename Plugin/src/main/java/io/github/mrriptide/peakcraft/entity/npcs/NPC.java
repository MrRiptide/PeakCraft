package io.github.mrriptide.peakcraft.entity.npcs;

import io.github.mrriptide.peakcraft.entity.CombatEntity;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

public abstract class NPC extends CombatEntity {

    protected NPC(String id, EntityType<? extends PathfinderMob> type, Level world) {
        super(id, type, world);
        setMaxHealth(1000000);
        setShowHealth(false);
    }

    @Override
    public void processAttack(CombatEntity attacker){
        // if attacked by an NPC or player, ignore
        if (!(attacker instanceof NPC || attacker instanceof PlayerWrapper)){
            super.processAttack(attacker);
        }
    }
}
