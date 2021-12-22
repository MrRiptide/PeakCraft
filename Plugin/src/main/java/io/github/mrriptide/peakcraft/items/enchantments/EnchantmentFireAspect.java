package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.AttackAction;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.WeaponItem;

import java.util.concurrent.ThreadLocalRandom;

public class EnchantmentFireAspect extends EnchantmentData {
    public EnchantmentFireAspect() {
        super("fire_aspect", "Fire Aspect", 4);
    }

    @Override
    public int getCost(int level) {
        return level;
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof AttackAction;
    }

    @Override
    public void onAction(Action action, int level) {
        if (ThreadLocalRandom.current().nextDouble() < 0.25*level){
            ((AttackAction)action).getDamagedAction().getPrimaryEntity().getEntity().setFireTicks(20*level);
        }
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return item instanceof WeaponItem;
    }
}
