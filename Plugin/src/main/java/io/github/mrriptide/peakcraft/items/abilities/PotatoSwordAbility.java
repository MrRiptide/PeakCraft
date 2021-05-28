package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.Item;
import org.bukkit.entity.Player;

public class PotatoSwordAbility extends Ability {
    public PotatoSwordAbility() {
        super("potato_sword_ability",
                "Potato Swarm",
                "Summons a swarm of potatoes to protect you. This is a really long ability description. I hope the wrapping works :)",
                50);
    }

    @Override
    public void useAbility(PlayerWrapper player) {
        player.giveItem("potato");
    }
}
