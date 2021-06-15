package io.github.mrriptide.peakcraft.entity.pathfinding;

import io.github.mrriptide.peakcraft.entity.CombatEntity;
import net.minecraft.server.v1_16_R3.*;

import java.util.EnumSet;

public class PathfinderGoalPotatoKing extends PathfinderGoal {
    private final CombatEntity entity; // the hostile entity
    private EntityLiving target; // the current target

    private final double speed; // speed
    private final double distance; // distance from target

    private double x; // target x
    private double y; // target y
    private double z; // target z

    private enum Phase {RANGE, BOMBER, MELEE}
    private Phase currentPhase;
    private long startPhaseTime;

    public PathfinderGoalPotatoKing(CombatEntity a, double speed, float distance) {
        this.entity = a;
        this.speed = speed;
        this.distance = distance;
        this.a(EnumSet.of(Type.MOVE));
        this.currentPhase = Phase.MELEE;
        this.startPhaseTime = System.currentTimeMillis();
    }

    @Override
    public boolean a() {
        // Will start the pathfinding goal if it's true.
        // runs every tick

        // okay so i will need to write the custom code for targeting here
        this.target = this.entity.getGoalTarget(); // Gets the boss's target.

        if (this.target == null)
            return false;
        double distanceToTarget = this.target.h(this.entity);

        if (System.currentTimeMillis() > startPhaseTime + 30*1000){
            if (currentPhase == Phase.BOMBER){
                if (this.target.h(this.entity) > 15){
                    currentPhase = Phase.RANGE;
                } else {
                    currentPhase = Phase.MELEE;
                }
            } else {
                currentPhase = Phase.BOMBER;
            }
        }

        if (currentPhase == Phase.RANGE || currentPhase == Phase.MELEE){
            this.x = this.target.locX();
            this.y = this.target.locY();
            this.z = this.target.locZ();
        }

        if (currentPhase == Phase.MELEE){
            if (distanceToTarget < getAttackDistance(target)){
                this.entity.swingHand(EnumHand.MAIN_HAND);
                this.entity.attackEntity(target);
            }
        } else if (currentPhase == Phase.RANGE) {
            if (distanceToTarget > 7) {
                this.entity.swingHand(EnumHand.MAIN_HAND);
                this.entity.attackEntity(target);
            }
        }
        return true;
    }

    @Override
    public void c() { // runs when a() returns true.
        // runner                   x       y      z     speed
        this.entity.getNavigation().a(this.x, this.y, this.z, this.speed);

    }

    @Override
    public boolean b() {
        // runs after c()
        // runs every tick as long as its true (repeats c)
        return !this.entity.getNavigation().m() && this.target.h(this.entity) < (double) (this.distance * this.distance);
    }

    @Override
    public void d() {
        // runs when b() returns false.
        this.target = null;
    }

    protected double getAttackDistance(EntityLiving var0) {
        return (double)(this.entity.getWidth() * 2.0F * this.entity.getWidth() * 2.0F + var0.getWidth());
    }
}
