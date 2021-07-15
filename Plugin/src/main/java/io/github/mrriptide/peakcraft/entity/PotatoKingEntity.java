package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.CustomColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

public class PotatoKingEntity extends HostileEntity {
    public PotatoKingEntity(Location loc) {
        super("potato_king", EntityType.ZOMBIE, ((CraftWorld) loc.getWorld()).getHandle());

        this.setSlot(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy((new Item("diamond_chestplate")).getItemStack()), true);
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        setName(CustomColors.BOSS_ENTITY +"Potato King");
        setMaxHealth(100000);
        this.strength = 150;

    }


}
