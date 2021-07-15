package io.github.mrriptide.peakcraft.entity.pathfinding;

import io.github.mrriptide.peakcraft.entity.CombatEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PathfinderGoalPotatoKing extends Goal {
    private final CombatEntity entity; // the hostile entity
    private LivingEntity target; // the current target

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
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.currentPhase = Phase.MELEE;
        this.startPhaseTime = System.currentTimeMillis();
    }

    @Override
    public boolean canUse() {
        // Will start the pathfinding goal if it's true.
        // runs every tick

        // okay so i will need to write the custom code for targeting here
        this.target = this.entity.getTarget(); // Gets the boss's target.

        if (this.target == null)
            return false;
        double distanceToTarget = this.target.distanceTo(this.entity);

        if (System.currentTimeMillis() > startPhaseTime + 30*1000){
            if (currentPhase == Phase.BOMBER){
                if (this.target.distanceTo(this.entity) > 15){
                    currentPhase = Phase.RANGE;
                } else {
                    currentPhase = Phase.MELEE;
                }
            } else {
                currentPhase = Phase.BOMBER;
            }
        }

        if (currentPhase == Phase.RANGE || currentPhase == Phase.MELEE){
            this.x = this.target.getX();
            this.y = this.target.getY();
            this.z = this.target.getZ();
        }

        if (currentPhase == Phase.MELEE){
            if (distanceToTarget < getAttackDistance(target)){
                this.entity.swing(InteractionHand.MAIN_HAND);
                this.entity.doHurtTarget(target);
            }
        } else if (currentPhase == Phase.RANGE) {
            if (distanceToTarget > 7) {
                this.entity.swing(InteractionHand.MAIN_HAND);
                this.entity.doHurtTarget(target);
            }
        }
        return true;
    }

    @Override
    public void tick() { // runs when a() returns true.
        // runner                   x       y      z     speed
        this.entity.getNavigation().moveTo(this.x, this.y, this.z, this.speed);

    }

    @Override
    public boolean canContinueToUse() {
        // runs after c()
        // runs every tick as long as its true (repeats c)
        return !this.entity.getNavigation().isDone() && this.target.distanceTo(this.entity) < (double) (this.distance * this.distance);
    }

    @Override
    public void stop() {
        // runs when b() returns false.
        this.target = null;
    }

    protected double getAttackDistance(LivingEntity entity) {
        return (double)(this.entity.getBbWidth() * 2.0F * this.entity.getBbWidth() * 2.0F + entity.getBbWidth());
    }
}
