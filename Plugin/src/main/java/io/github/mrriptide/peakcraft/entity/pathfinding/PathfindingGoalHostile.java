package io.github.mrriptide.peakcraft.entity.pathfinding;

import io.github.mrriptide.peakcraft.entity.CombatEntity;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.PathfinderGoalZombieAttack;

import java.util.EnumSet;

public class PathfindingGoalHostile extends PathfinderGoal {
    private final CombatEntity a;

    public PathfindingGoalHostile(CombatEntity a, double speed, float distance) {
        this.a = a;
        this.a(EnumSet.of(Type.MOVE));
    }

    @Override
    public boolean a() {
        return false;
    }
}
