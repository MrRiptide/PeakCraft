package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.PlayerTickAction;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.EnchantableItem;

public class EnchantmentDepthStrider extends EnchantmentData {
    public EnchantmentDepthStrider() {
        super("depth_strider", "Depth Strider", 3);
        this.isTreasure = true;
    }

    @Override
    public int getCost(int level) {
        return level;
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof PlayerTickAction;
    }

    @Override
    public void onAction(Action action, int level) {
        if (action.getPrimaryEntity() instanceof PlayerWrapper && (action.getPrimaryEntity().getEntity()).isInWater()){
            ((PlayerWrapper)action.getPrimaryEntity()).getStatus().getSpeed().addAdditive(50);
        }
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return item.getType().equalsIgnoreCase("boots");
    }
}
