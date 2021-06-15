package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.entity.pathfinding.PathfinderGoalAngeredTarget;
import io.github.mrriptide.peakcraft.entity.pathfinding.PathfinderGoalHostileTarget;
import net.minecraft.server.v1_16_R3.*;

import java.util.HashMap;
import java.util.UUID;

public abstract class HostileEntity extends CombatEntity{
    protected HashMap<UUID, Double> anger = new HashMap<>();
    protected HostileEntity(String id, EntityTypes<? extends EntityCreature> type, World world) {
        super(id, type, world);
    }

    @Override
    public void initPathfinder() { // This method will apply some custom pathfinders to our pig

        //PathfinderGoalNearestAttackableTarget
        /*
         * this.targetSelector - Communicates what the pig's target to walk to will be.
         *
         * .a(0, pathfinder) - The pig's ai.
         *
         * 0, - priority
         * new PathfinderGoalNearestAttackableTarget<EntityHuman> - Tells the pig to target the nearest attackable target - <EntityHuman>
         * sets the type of mob to select.
         *
         * (this, - the pig
         * EntityHuman.class, - what to target
         * true) - this part is weird, but it tells the pig weather to target its own kind. We really don't need to worry about this, but
         * if we used EntityCreature as the target this would stop the pig from attacking itself.
         */
        //this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true));

        /*
         * this.goalSelector - Communicates what the pig's goal to perform will be.
         *
         * PathfinderGoalLeapAtTarget( - will make the pig leap at the player similar to a wolf attacking(this will not to damage)
         * this, - the pig
         * 1.0f) - the height of the jump(Please experiment with this to get a height you want)
         */
        this.targetSelector.a(0, new PathfinderGoalAngeredTarget(this, false));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0, true));
        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));

    }

    public HashMap<UUID,Double> getAnger(){return anger;}
    public double getPlayerAnger(UUID player){return anger.getOrDefault(player, 0.0);}
    public void setPlayerAnger(UUID player, double angerValue){
        this.anger.put(player, angerValue);
    }

    @Override
    public void processAttack(CombatEntity attacker) {
        if (attacker instanceof PlayerWrapper){
            anger.put(((PlayerWrapper) attacker).getSource().getPlayer().getUniqueId(), anger.getOrDefault(attacker.getUniqueID(), 0.0) + 50);
        }

        super.processAttack(attacker);
    }
}
