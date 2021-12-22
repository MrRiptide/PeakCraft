package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.*;
import io.github.mrriptide.peakcraft.entity.wrappers.CombatEntityWrapper;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import org.bukkit.event.entity.EntityDamageEvent;

public class EnchantmentThorns extends EnchantmentData {
    public EnchantmentThorns() {
        super("thorns", "Thorns", 5);
    }

    @Override
    public int getCost(int level) {
        return level;
    }

    @Override
    public ActionListener.PriorityLevel getListeningLevel(){
        return ActionListener.PriorityLevel.LAST;
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof DamagedAction && ((DamagedAction) action).getAttacker() != null;
    }

    @Override
    public void onAction(Action action, int level) {
        double damage = ((DamagedAction)action).getDamage().getDamage(Damage.DamageType.MELEE).getFinal();
        if (damage > 0){
            DamagedAction thornsAction = new DamagedAction(((DamagedAction)action).getAttacker(), EntityDamageEvent.DamageCause.THORNS, damage * 0.02 * level);
            thornsAction.runAction();
        }
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return false;
    }
}
