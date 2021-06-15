package io.github.mrriptide.peakcraft.entity.pathfinding;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.CombatEntity;
import io.github.mrriptide.peakcraft.entity.HostileEntity;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class PathfinderGoalAngeredTarget extends PathfinderGoalTarget {
    protected PathfinderTargetCondition d;
    protected EntityLiving target;

    public PathfinderGoalAngeredTarget(EntityInsentient entityinsentient, boolean flag) {
        this(entityinsentient, flag, false);
    }

    public PathfinderGoalAngeredTarget(EntityInsentient entityinsentient, boolean flag, boolean flag1) {
        this(entityinsentient, flag, flag1, (Predicate)null);
    }

    public PathfinderGoalAngeredTarget(EntityInsentient entityinsentient, boolean flag, boolean flag1, @Nullable Predicate<EntityLiving> predicate) {
        super(entityinsentient, flag, flag1);
        this.a(EnumSet.of(Type.TARGET));
        this.d = (new PathfinderTargetCondition()).a(this.k()).a(predicate);
    }

    @Override
    public boolean a() {
        // determines if pathfinding should continue
        this.g();
        return this.target != null;
    }

    public void g(){
        // select target
        UUID maxPlayer = null;
        List<Entity> entities =  this.e.getBukkitEntity().getNearbyEntities(15, 15, 15);
        HashMap<UUID, Double> angers = ((HostileEntity)this.e).getAnger();
        double currentAngerAmount = -100;
        if (this.e.getGoalTarget() instanceof EntityPlayer){
            maxPlayer = ((EntityPlayer) this.e.getGoalTarget()).getBukkitEntity().getUniqueId();
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
    public void c() {
        this.e.setGoalTarget(this.target, this.target instanceof EntityPlayer ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
        super.c();
    }
}
