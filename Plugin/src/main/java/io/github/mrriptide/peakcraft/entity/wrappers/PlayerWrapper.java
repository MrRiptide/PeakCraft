package io.github.mrriptide.peakcraft.entity.wrappers;

import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerWrapper extends CombatEntityWrapper{
    protected double mana;
    protected double maxMana;
    protected double critChance;
    protected double critDamage;

    public PlayerWrapper(Player player){
        super(player);
        this.mana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "mana", 0.0);
        this.maxMana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "maxMana", 100.0);
        this.critChance = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "critChance", 0.5);
        this.critDamage = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "critDamage", 0.5);
        this.name = player.getName();
        this.id = "player";
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "mana", this.mana);
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "critChance", this.critChance);
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "critDamage", this.critDamage);
    }
}
