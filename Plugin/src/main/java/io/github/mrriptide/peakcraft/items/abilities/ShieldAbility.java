package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.Damage;
import io.github.mrriptide.peakcraft.actions.DamagedAction;
import io.github.mrriptide.peakcraft.actions.RightClickAction;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.abilities.triggers.AbilityTrigger;
import org.bukkit.entity.Player;

public class ShieldAbility extends Ability {
    public ShieldAbility() {
        super("shield_ability",
                AbilityType.RIGHT_CLICK,
                "Shield",
                "Protects the player from incoming physical damage when active",
                0,
                0);
    }

    @Override
    public boolean listensTo(Action action) {
        return action instanceof RightClickAction;
    }

    @Override
    public PriorityLevel getListeningLevel() {
        return PriorityLevel.LAST;
    }

    @Override
    public void onAction(Action action) {
        ((PlayerWrapper)action.getPrimaryEntity()).getStatus().setBlocking(true);
    }

    @Override
    public void useAbility(PlayerWrapper player, AbilityTrigger trigger) {

    }
}
