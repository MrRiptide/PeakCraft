package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.PlayerTickAction;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Objects;

public class EnchantmentFrostWalker extends EnchantmentData {
    public EnchantmentFrostWalker() {
        super("frost_walker", "Frost Walker", 3);
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
        Block block = action.getPrimaryEntity().getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(-level, 0, -level);
        for (int i = 0; i < 2*level + 1; i++){
            for (int j = 0; j < 2*level + 1; j++){
                if (block.getType() == Material.WATER || block.getType() == Material.FROSTED_ICE){
                    block.setType(Material.FROSTED_ICE);
                }
                block = block.getRelative(1, 0, 0);
            }
            block = block.getRelative(-(2*level+1), 0, 1);
        }
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return Objects.equals(item.getType(), "Boots");
    }
}
