package io.github.mrriptide.peakcraft.entity.pathfinding;

import io.github.mrriptide.peakcraft.entity.CombatEntity;
import io.github.mrriptide.peakcraft.entity.HostileEntity;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
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
        CraftPlayer maxPlayer = null;
        for (Map.Entry<UUID, Double> entry : ((HostileEntity)this.e).getAnger().entrySet()){
            if (maxPlayer == null){
                maxPlayer = (CraftPlayer) Bukkit.getPlayer(entry.getKey());
            } else {
                if (maxPlayer.getLocation().distance())
            }
        }
    }

    @Override
    public void c() {
        this.e.setGoalTarget(this.target, this.target instanceof EntityPlayer ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
        super.c();
    }
}
