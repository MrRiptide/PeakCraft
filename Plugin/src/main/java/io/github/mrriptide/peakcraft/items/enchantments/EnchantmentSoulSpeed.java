package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.PlayerTickAction;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.List;

public class EnchantmentSoulSpeed extends EnchantmentData {
    public EnchantmentSoulSpeed() {
        super("soul_speed", "Soul Speed", 3);
    }

    @Override
    public int getCost(int level) {
        return 1 * level;
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof PlayerTickAction;
    }

    @Override
    public void onAction(Action action, int level) {
        List<Material> speedMaterials = Arrays.asList(Material.SOUL_SAND, Material.SOUL_SOIL);
        if (speedMaterials.contains(action.getPrimaryEntity().getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType())){
            ((PlayerWrapper)action.getPrimaryEntity()).getStatus().getSpeed().addAdditive(100);
        }
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return item.getType().equals("Boots");
    }
}
