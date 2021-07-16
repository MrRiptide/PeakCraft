package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
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
            player.giveItem("potato");
        }
    }
}
