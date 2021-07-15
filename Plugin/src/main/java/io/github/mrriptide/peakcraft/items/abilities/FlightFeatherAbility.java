package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.abilities.triggers.AbilityTrigger;
import io.github.mrriptide.peakcraft.items.abilities.triggers.TickAbilityTrigger;

public class FlightFeatherAbility extends Ability{
    public FlightFeatherAbility() {
        super("flight_feather_ability",
                AbilityType.PASSIVE,
                "Flight of the Feather",
                "Allows the player to fly at a cost of 20 mana per second",
                0,
                0);
    }

    @Override
    public boolean canUseAbility(PlayerWrapper player, AbilityTrigger trigger){
        return trigger instanceof TickAbilityTrigger && super.canUseAbility(player, trigger);
    }

    @Override
    public void useAbility(PlayerWrapper player, AbilityTrigger trigger) {
        player.getStatus().setFlightAllowed(player.getMana() >= 1);

        if (player.getStatus().isFlying()){
            player.reduceMana(1);
        }
    }
}
