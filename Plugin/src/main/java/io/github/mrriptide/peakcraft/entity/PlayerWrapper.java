package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.CombatEntity;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Date;

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
    protected long lastDamageTime;

    public PlayerWrapper(Player player){
        super(EntityTypes.SHEEP, ((CraftWorld) player.getWorld()).getHandle());
        this.source = player;
        this.maxHealth = 100;

        double intelligence = 0;

        for (ItemStack itemStack : player.getInventory().getArmorContents()){
            if (itemStack != null && itemStack.getType() != Material.AIR){
                EnchantableItem item = new EnchantableItem(itemStack);
                item.bakeAttributes();
                this.maxHealth += item.getBakedAttribute("health");
                intelligence += item.getBakedAttribute("intelligence");
            }
        }

        this.maxMana = 100 + intelligence;

        this.health = Math.min(PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "health", maxHealth), maxHealth);
        this.mana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "mana", 0.0);
        this.maxMana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "maxMana", 100.0);
        this.lastDamageTime = PersistentDataManager.getValueOrDefault(player, PersistentDataType.LONG, "lastDamageTime", Long.valueOf(0));
        this.critChance = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "critChance", 0.5);
        this.critDamage = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "critDamage", 0.5);
        this.name = player.getName();
        this.weapon = (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) ? new EnchantableItem(player.getInventory().getItemInMainHand()) : null;
    }

    public void processDamage(double amount){
        lastDamageTime = (new Date()).getTime();
        super.processDamage(amount);
    }

    public void tryNaturalRegen(){
        if ((new Date()).getTime() - lastDamageTime > 5000){
            regenHealth(maxHealth / 100 * 2.5);
        }
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "mana", this.mana);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "critChance", this.critChance);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "critDamage", this.critDamage);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.LONG, "lastDamageTime", this.lastDamageTime);
        sendActionBar();
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

    public void sendActionBar(){
        this.source.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "" + (int)this.health + "/" + (int)this.maxHealth + "❤            " + ChatColor.AQUA + (int)mana +  "/" + (int)maxMana + "MP"));
    }
}
