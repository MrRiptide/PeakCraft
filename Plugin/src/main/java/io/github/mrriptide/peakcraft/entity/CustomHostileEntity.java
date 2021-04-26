package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.items.Item;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;

public class CustomHostileEntity extends CustomDamageableEntity {
    protected double strength;
    protected Item weapon;

    protected CustomHostileEntity(EntityTypes<? extends EntityCreature> type, World world) {
        super(type, world);
    }
}
