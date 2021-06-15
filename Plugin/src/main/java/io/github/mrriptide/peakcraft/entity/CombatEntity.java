package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.persistence.PersistentDataType;

public abstract class CombatEntity extends LivingEntity {
    protected double strength;
    protected Item weapon;

    protected CombatEntity(String id, EntityTypes<? extends EntityCreature> type, World world) {
        super(id, type, world);
    }

    public void applyNBT(){
        super.applyNBT();
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "strength", strength);
    }

    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "strength", this.strength);

        /* not sure if this code is important but it is causing problems
        if (this.weapon != null && this.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity && ((org.bukkit.entity.LivingEntity)this.getBukkitEntity()).getEquipment() != null){
            ((org.bukkit.entity.LivingEntity)this.getBukkitEntity()).getEquipment().setItemInMainHand(weapon.getItemStack());
        }*/
    }

    public Item getWeapon(){
        return weapon;
    }

    public double getStrength(){
        return strength;
    }
}
