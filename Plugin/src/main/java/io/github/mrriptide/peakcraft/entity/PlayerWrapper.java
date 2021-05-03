package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.entity.CombatEntity;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlayerWrapper extends CombatEntity {
    public double getCritChance() {
        return critChance;
    }

    public double getCritDamage() {
        return critDamage;
    }

    public Player getSource() {
        return source;
    }

    protected Player source;
    protected double mana;
    protected double maxMana;
    protected double critChance;
    protected double critDamage;

    public PlayerWrapper(Player player){
        super(EntityTypes.SHEEP, ((CraftWorld) player.getWorld()).getHandle());
        this.source = player;
        this.maxHealth = 100;

        double intelligence = 0;

        for (ItemStack itemStack : player.getInventory().getArmorContents()){
            Item item = new Item(itemStack);
            this.maxHealth += item.getBakedAttribute("health");
            intelligence += item.getBakedAttribute("intelligence");
        }

        this.maxMana = 100 + intelligence;

        this.health = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "health", maxHealth);
        this.mana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "mana", 0.0);
        this.maxMana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "maxMana", 100.0);
        this.critChance = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "critChance", 0.5);
        this.critDamage = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "critDamage", 0.5);
        this.name = player.getName();
        this.weapon = (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) ? new Item(player.getInventory().getItemInMainHand()) : null;
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "mana", this.mana);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "critChance", this.critChance);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "critDamage", this.critDamage);
    }

    @Override
    public CraftEntity getBukkitEntity() {
        return (CraftEntity) source;
    }

    public void resetStats(){
        this.health = maxHealth;
        this.mana = maxMana;
        updateEntity();
    }
}
