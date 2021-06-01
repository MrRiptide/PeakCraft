package io.github.mrriptide.peakcraft.entity.pathfinding;

import io.github.mrriptide.peakcraft.entity.CombatEntity;
import net.minecraft.server.v1_16_R3.*;

import java.util.EnumSet;

public class PathfinderGoalHostileTarget extends PathfinderGoal {
    private final CombatEntity entity; // the hostile entity
    private EntityLiving target; // the current target

    private final double speed; // speed
    private final double distance; // distance from target

    private double x; // target x
    private double y; // target y
    private double z; // target z

    public PathfinderGoalHostileTarget(CombatEntity a, double speed, float distance) {
        this.entity = a;
        this.speed = speed;
        this.distance = distance;
        this.a(EnumSet.of(Type.MOVE));
    }

    @Override
    public boolean a() {
        // Will start the pathfinding goal if it's true.
        // runs every tick

        // okay so i will need to write the custom code for targeting here
        this.target = this.entity.getGoalTarget(); // Makes the player the pigs target.

        // Now we need to add several checks to prevent bugs.
        if (this.target == null) { // Checks if the pet is null.
            return false; // Stops the goal if the pet is null.
        } /*else if (this.entity.getDisplayName() == null) {
            return false;
        } /*else if (!(this.a.getDisplayName().toString().contains(this.target.getName()))) { // Checks if the pet's name contains the owners name.
            return false;                                                              // This is how we tell the pet who to follow.

        }*/ else if (this.target.h(this.entity) > (double) (this.distance * this.distance)) { // Checks the pets distance from the player.
            entity.setPosition(this.target.locX(), this.target.locY(), this.target.locZ()); // Teleport the pet to the player if he gets to far away.
            return false;
        } else { // Makes pet follow the player

            Vec3D vec = RandomPositionGenerator.a((EntityCreature) this.entity, 16, 7, this.target.getPositionVector());
            // In air check using Vec3D
            if (vec == null) {
                return false;
            }

            this.x = this.target.locX();
            this.y = this.target.locY();
            this.z = this.target.locZ();

            return true;// <-- runs c()
        }
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
}
