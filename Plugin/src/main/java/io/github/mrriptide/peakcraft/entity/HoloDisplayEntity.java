package io.github.mrriptide.peakcraft.entity;

//import net.minecraft.server.v1_17_R1.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class HoloDisplayEntity extends LivingEntity {
    public HoloDisplayEntity(Location location, String text) {
        super(EntityType.ARMOR_STAND, ((CraftWorld) location.getWorld()).getHandle());
        this.setPos(location.getX(), location.getY(), location.getZ());
        //this.setSlot(EnumItemSlot.MAINHAND, new org.bukkit.inventory.ItemStack(Material.AIR, 1));
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.setNoGravity(true);
        this.setCustomName(new TextComponent(text));
        this.setCustomNameVisible(true);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return null;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    /*public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        switch($SWITCH_TABLE$net$minecraft$world$entity$EnumItemSlot$Function()[enumitemslot.a().ordinal()]) {
            case 1:
                return (ItemStack)this.handItems.get(enumitemslot.b());
            case 2:
                return (ItemStack)this.armorItems.get(enumitemslot.b());
            default:
                return ItemStack.b;
        }
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.setSlot(enumitemslot, itemstack, false);
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack, boolean silent) {
        switch($SWITCH_TABLE$net$minecraft$world$entity$EnumItemSlot$Function()[enumitemslot.a().ordinal()]) {
            case 1:
                this.playEquipSound(itemstack, silent);
                this.handItems.set(enumitemslot.b(), itemstack);
                break;
            case 2:
                this.playEquipSound(itemstack, silent);
                this.armorItems.set(enumitemslot.b(), itemstack);
        }

    }*/
}
