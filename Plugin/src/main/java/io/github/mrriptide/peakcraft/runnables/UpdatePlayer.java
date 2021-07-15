package io.github.mrriptide.peakcraft.runnables;

import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.abilities.Ability;
import io.github.mrriptide.peakcraft.items.abilities.AbilityManager;
import io.github.mrriptide.peakcraft.items.abilities.triggers.TickAbilityTrigger;
import io.github.mrriptide.peakcraft.util.Tablist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Player Tick Updater
 *
 * Should be called once every tick per player
 *
 * **/
public class UpdatePlayer extends BukkitRunnable {
    public static int ticksPerUpdate = 1;
    private final UUID playerUUID;
    private Tablist playerTablist;
    public UpdatePlayer(Player player){
        this.playerUUID = player.getUniqueId();
    }

    @Override
    public void run() {
        Player player = Bukkit.getServer().getPlayer(playerUUID);
        if (player != null){
            PlayerWrapper wrapper = new PlayerWrapper(player);
            if (playerTablist == null){
                playerTablist = new Tablist(wrapper);
            }

            wrapper.tryNaturalRegen();
            wrapper.regenMana();

            if (wrapper.getFullSet() != null){
                wrapper.getFullSet().applyBonus(wrapper);
            }

            for (Item abilityItem : wrapper.getAbilityItems()){
                if (abilityItem.getAbility().getType() == Ability.AbilityType.PASSIVE){
                    AbilityManager.triggerAbility(abilityItem.getAbility(), wrapper, new TickAbilityTrigger());
                }
            }

            wrapper.getStatus().apply();

            wrapper.updateEntity();
        } else {
            this.cancel();
        }
    }
}
