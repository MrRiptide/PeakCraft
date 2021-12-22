package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.AttackAction;
import io.github.mrriptide.peakcraft.actions.Damage;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.WeaponItem;

public class EnchantmentSharpness extends EnchantmentData {
    public EnchantmentSharpness(){
        super("sharpness", "Sharpness", 5);
    }

    @Override
    public int getCost(int level) {
        return 3 * level;
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return (item instanceof WeaponItem);
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof AttackAction;
    }

    @Override
    public void onAction(Action action, int level) {
        ((AttackAction)action).getDamage().getDamage(Damage.DamageType.MELEE).addMulti(Math.pow(1.05, level) - 1);
    }
}
