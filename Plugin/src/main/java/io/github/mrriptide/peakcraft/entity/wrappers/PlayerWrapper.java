package io.github.mrriptide.peakcraft.entity.wrappers;

import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerWrapper extends CombatEntityWrapper{
    protected double mana;
    protected double maxMana;

    public PlayerWrapper(Player player){
        super(player);
        this.mana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "mana", 0);
        this.maxMana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "maxMana", 100);
        this.name = player.getName();
        this.id = "player";
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "mana", this.mana);
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "health", this.health);
    }
}
