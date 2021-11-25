package io.github.mrriptide.peakcraft.entity.wrappers;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.util.Attribute;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CombatEntityWrapper extends LivingEntityWrapper {
    protected Attribute strength;
    protected Item weapon;

    protected CombatEntityWrapper(){
        super();
    }

    public CombatEntityWrapper(LivingEntity entity) throws EntityException {
        super(entity);
        this.strength = PersistentDataManager.getAttribute(entity, "strength", 0.0);
    }

    public void applyNBT(){
        super.applyNBT();
        PersistentDataManager.setAttribute(entity, "strength", this.strength);
    }

    @Override
    public void resetAttributes(){
        super.resetAttributes();
        this.strength.reset();
    }

    @Override
    public void updateAttributes(){
        super.updateAttributes();
        try{
            ItemStack weaponItem = ((LivingEntity)entity).getEquipment().getItemInMainHand();
            this.weapon = !(weaponItem.getType().equals(Material.AIR)) ? ItemManager.convertItem(weaponItem) : null;
        } catch (ItemException e) {
            PeakCraft.getPlugin().getLogger().warning("Entity " + entity.getName() + " has an invalid item in their hand!");
            this.weapon = null;
        }
    }

    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setAttribute(entity, "strength", this.strength);

        /* not sure if this code is important but it is causing problems
        if (this.weapon != null && this.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity && ((org.bukkit.entity.LivingEntity)this.getBukkitEntity()).getEquipment() != null){
            ((org.bukkit.entity.LivingEntity)this.getBukkitEntity()).getEquipment().setItemInMainHand(weapon.getItemStack());
        }*/
    }

    public Item getWeapon(){
        return weapon;
    }

    public Attribute getStrength(){
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
