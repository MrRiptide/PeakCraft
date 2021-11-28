package io.github.mrriptide.peakcraft.entity.player;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public abstract class PlayerManager {
    private static HashMap<UUID, PlayerWrapper> playerAccounts = new HashMap<>();

    public static PlayerWrapper getPlayer(Player player){
        return getPlayer(player.getUniqueId());
    }

    public static PlayerWrapper getPlayer(UUID uuid){
        if (playerAccounts.containsKey(uuid)){
            return playerAccounts.get(uuid);
        } else {
            throw new IllegalArgumentException("Player " + uuid.toString() + " is not currently logged in");
        }
    }

    public static void logInPlayer(Player player) {
        try {
            playerAccounts.put(player.getUniqueId(), new PlayerWrapper(player));
        } catch (EntityException e) {
            PeakCraft.getPlugin().getLogger().warning("Attempted to log in a player but something failed in wrapping! Please report this to the developers");
            e.printStackTrace();
        }
    }
}
