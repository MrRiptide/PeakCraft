package io.github.mrriptide.peakcraft.entity.pathfinding;

import io.github.mrriptide.peakcraft.entity.CombatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class PathfinderGoalHostileTarget extends Goal {
    private final CombatEntity entity; // the hostile entity
    private LivingEntity target; // the current target

    private final double speed; // speed
    private final double distance; // distance from target

    private double x; // target x
    private double y; // target y
    private double z; // target z

    public PathfinderGoalHostileTarget(CombatEntity a, double speed, float distance) {
        this.entity = a;
        this.speed = speed;
        this.distance = distance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Will start the pathfinding goal if it's true.
        // runs every tick

        // okay so i will need to write the custom code for targeting here
        this.target = this.entity.getTarget(); // Makes the player the pigs target.

        // Now we need to add several checks to prevent bugs.
        if (this.target == null) { // Checks if the pet is null.
            return false; // Stops the goal if the pet is null.
        } /*else if (this.entity.getDisplayName() == null) {
            return false;
        } /*else if (!(this.a.getDisplayName().toString().contains(this.target.getName()))) { // Checks if the pet's name contains the owners name.
            return false;                                                              // This is how we tell the pet who to follow.

        }*/ else if (this.target.distanceTo(this.entity) > (double) (this.distance * this.distance)) { // Checks the pets distance from the player.
            entity.setPos(this.target.getX(), this.target.getY(), this.target.getZ()); // Teleport the pet to the player if he gets to far away.
            return false;
        } else { // Makes pet follow the player

            BlockPos pos = RandomPos.generateRandomPosTowardDirection(this.entity, 16, new Random(), this.target.blockPosition());
            // In air check using Vec3D
            if (pos == null) {
                return false;
            }

            this.x = this.target.getX();
            this.y = this.target.getY();
            this.z = this.target.getZ();

            return true;// <-- runs c()
        }
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
}
