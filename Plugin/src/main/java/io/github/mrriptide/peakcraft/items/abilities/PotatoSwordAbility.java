package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.RightClickAction;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.abilities.triggers.AbilityTrigger;
import io.github.mrriptide.peakcraft.items.abilities.triggers.RightClickAbilityTrigger;

public class PotatoSwordAbility extends Ability {
    public PotatoSwordAbility() {
        super("potato_sword_ability",
                AbilityType.RIGHT_CLICK,
                "Potato Swarm",
                "Summons a swarm of potatoes to protect you. This is a really long ability description. I hope the wrapping works :)",
                50,
                0);
    }

    @Override
    public void useAbility(PlayerWrapper player, AbilityTrigger trigger) {
        if (trigger instanceof RightClickAbilityTrigger){
            try {
                player.giveItem("potato");
            } catch (ItemException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public PriorityLevel getListeningLevel() {
        return PriorityLevel.MIDDLE;
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof RightClickAction;
    }

    @Override
    public void onAction(Action action) {

    }
}
