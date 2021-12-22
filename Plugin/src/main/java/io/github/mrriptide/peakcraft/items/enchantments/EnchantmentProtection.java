package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.Damage;
import io.github.mrriptide.peakcraft.actions.DamagedAction;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.EnchantableItem;

public class EnchantmentProtection extends EnchantmentData {

    public EnchantmentProtection() {
        super("protection", "Protection", 5);
    }

    @Override
    public int getCost(int level) {
        return 3 * level;
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof DamagedAction;
    }

    @Override
    public void onAction(Action action, int level) {
        ((DamagedAction)action).getDamage().getProtection(Damage.DamageType.GENERIC).addMulti(level*0.02);
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return item instanceof ArmorItem;
    }
}
