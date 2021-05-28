package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.CustomColors;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;

public class PotatoKingEntity extends HostileEntity {
    public PotatoKingEntity(Location loc) {
        super(EntityTypes.ZOMBIE, ((CraftWorld) loc.getWorld()).getHandle());

        this.setSlot(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy((new Item("diamond_chestplate")).getItemStack()));
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        setName(CustomColors.BOSS_ENTITY +"Potato King");
        setMaxHealth(100000);
        this.strength = 150;

    }
}
