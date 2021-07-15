package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.entity.pathfinding.PathfinderGoalAngeredTarget;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class HostileEntity extends CombatEntity{
    protected HashMap<UUID, Double> anger = new HashMap<>();
    protected HostileEntity(String id, EntityType<? extends PathfinderMob> type, Level world) {
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
        this.targetSelector.addGoal(0, new PathfinderGoalAngeredTarget(this, false));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));

    }

    public HashMap<UUID,Double> getAnger(){return anger;}
    public double getPlayerAnger(UUID player){return anger.getOrDefault(player, 0.0);}
    public void setPlayerAnger(UUID player, double angerValue){
        this.anger.put(player, angerValue);
    }

    @Override
    public void processAttack(CombatEntity attacker) {
        if (attacker instanceof PlayerWrapper){
            anger.put(((PlayerWrapper) attacker).getSource().getPlayer().getUniqueId(), anger.getOrDefault(((PlayerWrapper) attacker).getSource().getPlayer().getUniqueId(), 0.0) + 50);
        }

        super.processAttack(attacker);
    }

    @Override
    public ArrayList<String> getData(){
        ArrayList<String> data = super.getData();

        data.add("");
        data.add("Anger levels:");
        for (UUID uuid : anger.keySet()){
            data.add("    " + uuid.toString() + ": " + anger.get(uuid).toString());
        }
        return data;
    }
}
