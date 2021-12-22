package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.AttackAction;
import io.github.mrriptide.peakcraft.actions.Damage;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.WeaponItem;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public class EnchantmentSmite extends EnchantmentData {
    public EnchantmentSmite() {
        super("smite", "Smite", 5);
    }

    @Override
    public int getCost(int level) {
        return 2 * level;
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof AttackAction;
    }

    @Override
    public void onAction(Action action, int level) {
        List<EntityType> undead = Arrays.asList(EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN,
                EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.WITHER_SKELETON);
        if (undead.contains(((AttackAction) action).getDamagedAction().getPrimaryEntity().getEntity().getType())){
            ((AttackAction)action).getDamage().getDamage(Damage.DamageType.MELEE).addMulti(Math.pow(1.10, level) - 1);
        }
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return (item instanceof WeaponItem);
    }
}
