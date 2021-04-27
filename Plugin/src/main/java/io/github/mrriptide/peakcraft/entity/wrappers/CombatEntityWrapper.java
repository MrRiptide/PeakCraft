package io.github.mrriptide.peakcraft.entity.wrappers;

import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public class CombatEntityWrapper extends HealthEntityWrapper{
    protected double strength;
    protected Item weapon;

    public CombatEntityWrapper(Entity entity){
        super(entity);
        this.strength = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.DOUBLE, "strength", 0.0);
        if (entity instanceof LivingEntity && ((LivingEntity)entity).getEquipment() != null && ((LivingEntity)entity).getEquipment().getItemInMainHand() != null){
            this.weapon = new Item(((LivingEntity)entity).getEquipment().getItemInMainHand());
        } else {
            this.weapon = null;
        }
    }

    public CombatEntityWrapper(CraftCreature creature){
        this(creature.getHandle().getBukkitEntity());
    }

    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "strength", this.strength);

        if (this.weapon != null && this.source instanceof LivingEntity && ((LivingEntity)this.source).getEquipment() != null){
            ((LivingEntity)this.source).getEquipment().setItemInMainHand(weapon.getItemStack());
        }
    }
}
