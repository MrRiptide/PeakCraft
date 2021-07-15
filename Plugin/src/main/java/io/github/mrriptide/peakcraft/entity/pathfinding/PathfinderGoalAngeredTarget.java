package io.github.mrriptide.peakcraft.entity.pathfinding;

import io.github.mrriptide.peakcraft.entity.HostileEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class PathfinderGoalAngeredTarget extends TargetGoal {
    protected TargetingConditions d;
    protected LivingEntity target;

    public PathfinderGoalAngeredTarget(Mob entityinsentient, boolean flag) {
        this(entityinsentient, flag, false);
    }

    public PathfinderGoalAngeredTarget(Mob entityinsentient, boolean flag, boolean flag1) {
        this(entityinsentient, flag, flag1, null);
    }

    public PathfinderGoalAngeredTarget(Mob entityinsentient, boolean flag, boolean flag1, @Nullable Predicate<LivingEntity> predicate) {
        super(entityinsentient, flag, flag1);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        //this.d = (new TargetingConditions()).a(this.k()).a(predicate);
    }

    @Override
    public boolean canUse() {
        // determines if pathfinding should continue
        this.selectTarget();
        return this.target != null;
    }

    public void selectTarget(){
        // select target
        UUID maxPlayer = null;
        List<Entity> entities =  this.mob.getBukkitEntity().getNearbyEntities(15, 15, 15);
        HashMap<UUID, Double> angers = ((HostileEntity)this.mob).getAnger();
        double currentAngerAmount = -100;
        if (this.mob.getTarget() instanceof net.minecraft.world.entity.player.Player){
            maxPlayer = ((net.minecraft.world.entity.player.Player) this.mob.getTarget()).getBukkitEntity().getUniqueId();
            currentAngerAmount = angers.getOrDefault(maxPlayer, 0.0);
        }
        //double currentAngerAmount = (this.e.getGoalTarget() instanceof EntityPlayer ? angers.getOrDefault(((EntityPlayer) this.e.getGoalTarget()).getBukkitEntity().getUniqueId(), 0.0) : 0.0);
        for (Entity entity : entities){
            if (entity instanceof Player){
                double potentialAnger = angers.getOrDefault(entity.getUniqueId(), 0.0);
                if (potentialAnger > currentAngerAmount + 25 && potentialAnger >= angers.getOrDefault(maxPlayer, 0.0)){
                    maxPlayer = entity.getUniqueId();
                }
            }
        }

        if (maxPlayer != null){
            this.target = ((CraftPlayer) Bukkit.getPlayer(maxPlayer)).getHandle();
        } else {
            this.target = null;
        }
    }

    @Override
    public void tick() {
        this.mob.setGoalTarget(this.target, this.target instanceof net.minecraft.world.entity.player.Player ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
        super.tick();
    }
}
