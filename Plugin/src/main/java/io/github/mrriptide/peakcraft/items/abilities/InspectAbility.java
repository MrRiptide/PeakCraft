package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.entity.LivingEntity;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.abilities.triggers.AbilityTrigger;
import io.github.mrriptide.peakcraft.items.abilities.triggers.RightClickAbilityTrigger;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreature;
import org.bukkit.event.block.Action;

public class InspectAbility extends Ability {
    public InspectAbility() {
        super("inspect_ability",
                AbilityType.RIGHT_CLICK,
                "Inspect",
                "Inspects a selected block or entity",
                0,
                0);
    }

    @Override
    public boolean canUseAbility(PlayerWrapper player, AbilityTrigger trigger){
        if (!(trigger instanceof RightClickAbilityTrigger)){
            return false;
        }
        if (((RightClickAbilityTrigger)trigger).hasEntity()){
            // send entity info
            return true;
        } else if (((RightClickAbilityTrigger)trigger).hasAction()){
            if (((RightClickAbilityTrigger)trigger).getAction() == Action.RIGHT_CLICK_AIR){
                player.getSource().sendMessage(CustomColors.ERROR + "Select a block or entity to use this");
                return false;
            } else if (((RightClickAbilityTrigger)trigger).getAction() == Action.RIGHT_CLICK_BLOCK){
                // send block info
                return true;
            }
        }
        return super.canUseAbility(player, trigger);
    }

    @Override
    public void useAbility(PlayerWrapper player, AbilityTrigger trigger) {
        if (((RightClickAbilityTrigger)trigger).hasEntity()){
            // send entity info
            if (((CraftCreature)((RightClickAbilityTrigger)trigger).getEntity()).getHandle() instanceof LivingEntity){
                LivingEntity entity = (LivingEntity) ((CraftCreature)((RightClickAbilityTrigger)trigger).getEntity()).getHandle();
                var entityData = entity.getData();
                StringBuilder message = new StringBuilder();
                message.append("\nEntity Data:");
                for (String line : entityData){
                    message.append(CustomColors.DEFAULT_CHAT + "\n" + line);
                }
                player.getSource().sendMessage(message.toString());
            } else {
                player.getSource().sendMessage(CustomColors.ERROR + "Not a custom entity");
            }
        } else if (((RightClickAbilityTrigger)trigger).hasBlock()){
            if (((RightClickAbilityTrigger)trigger).getAction() == Action.RIGHT_CLICK_BLOCK){
                // send block info
                player.getSource().sendMessage(((RightClickAbilityTrigger)trigger).getBlock().getBlockData().getAsString());
            }
        }
    }
}
