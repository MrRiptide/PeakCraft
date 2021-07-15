package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public abstract class CombatEntity extends LivingEntity {
    protected double strength;
    protected Item weapon;

    protected CombatEntity(String id, EntityType<? extends PathfinderMob> type, Level world) {
        super(id, type, world);
    }

    public void applyNBT(){
        super.applyNBT();
        PersistentDataManager.setValue(this.getBukkitEntity(), "strength", strength);
    }

    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setValue(this.getBukkitEntity(), "strength", this.strength);

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

    @Override
    public ArrayList<String> getData(){
        var data = super.getData();

        data.add("Strength: " + strength);
        if (weapon != null){
            data.add("Weapon: " + weapon.getDisplayName());
            data.add("Weapon Lore: \n" + weapon.getLore() + "\n");
        } else{
            data.add("Weapon: " + CustomColors.ERROR + "null");
        }


        return data;
    }
}
