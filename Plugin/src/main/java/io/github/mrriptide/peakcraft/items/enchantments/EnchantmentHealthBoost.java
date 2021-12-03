package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.DamagedAction;
import io.github.mrriptide.peakcraft.actions.PlayerTickAction;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.EnchantableItem;

public class EnchantmentHealthBoost extends Enchantment{
    public EnchantmentHealthBoost(int level){
        super("health_boost", "Health Boost", level);
    }

    public boolean validateEnchant(EnchantableItem item){
        return (item instanceof ArmorItem);
    }

    @Override
    public void onAction(Action action) {
        PlayerWrapper player = (PlayerWrapper) action.getPrimaryEntity();
        player.getEntityMaxHealth().addAdditive(20*level);
    }

    @Override
    public PriorityLevel getListeningLevel() {
        return PriorityLevel.MIDDLE;
    }

    @Override
    public boolean listensTo(Action action) {
        return true;
    }
}
