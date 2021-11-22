package io.github.mrriptide.peakcraft.entity.wrappers;

import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class CombatEntityWrapper extends LivingEntityWrapper {
    protected double strength;
    protected Item weapon;

    protected CombatEntityWrapper(){
        super();
    }

    public CombatEntityWrapper(LivingEntity entity) throws EntityException {
        super(entity);
        this.strength = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.DOUBLE, "strength", 0.0);
    }

    public void applyNBT(){
        super.applyNBT();
        PersistentDataManager.setValue(entity, "strength", strength);
    }

    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setValue(entity, "strength", this.strength);

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
