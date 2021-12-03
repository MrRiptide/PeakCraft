package io.github.mrriptide.peakcraft.runnables;

import io.github.mrriptide.peakcraft.actions.PlayerTickAction;
import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.abilities.Ability;
import io.github.mrriptide.peakcraft.items.abilities.AbilityManager;
import io.github.mrriptide.peakcraft.items.abilities.triggers.TickAbilityTrigger;
import io.github.mrriptide.peakcraft.util.Tablist;
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
        try{
            PlayerWrapper wrapper = PlayerManager.getPlayer(playerUUID);

            wrapper.updateFromEntity();
            PlayerTickAction action = new PlayerTickAction(wrapper);
            action.runAction();

            /*
            Tab list not being used for now because i dont want to have to deal with it

            if (playerTablist == null){
                playerTablist = new Tablist(wrapper);
            }*/

            if (wrapper.getEntityHealth() <= 0){
                return;
            }

            wrapper.getStatus().init();
            wrapper.tryNaturalRegen();
            wrapper.regenMana();

            if (wrapper.getFullSet() != null){
                wrapper.getFullSet().applyBonus(wrapper);
            }

            wrapper.getStatus().apply();

            wrapper.updateEntity();
        } catch (IllegalArgumentException e) {
            this.cancel();
        }
    }
}
