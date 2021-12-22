package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.EnchantableItem;

public class EnchantmentHealthBoost extends EnchantmentData{
    public EnchantmentHealthBoost(){
        super("health_boost", "Health Boost", 5);
    }

    @Override
    public int getCost(int level) {
        return 2 * level;
    }

    public boolean validateEnchant(EnchantableItem item){
        return (item instanceof ArmorItem);
    }

    @Override
    public void onAction(Action action, int level) {
        PlayerWrapper player = (PlayerWrapper) action.getPrimaryEntity();
        player.getEntityMaxHealth().addAdditive(20 * level);
    }

    @Override
    public boolean listensTo(Action action) {
        return true;
    }
}
