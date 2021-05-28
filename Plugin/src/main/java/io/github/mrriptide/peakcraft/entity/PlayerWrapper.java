package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.CombatEntity;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonus;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonusManager;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EntityTypes;
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
                Item item = ItemManager.convertItem(itemStack);
                if (item instanceof EnchantableItem){
                    ((EnchantableItem)item).bakeAttributes();
                    this.maxHealth += ((EnchantableItem)item).getBakedAttribute("health");
                    intelligence += ((EnchantableItem)item).getBakedAttribute("intelligence");
                }
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
        this.weapon = (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) ? ItemManager.convertItem(player.getInventory().getItemInMainHand()) : null;
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
        this.source.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CustomColors.HEALTH + "" + (int)this.health + "/" + (int)this.maxHealth + "❤            " + CustomColors.MANA + (int)mana +  "/" + (int)maxMana + "MP"));
    }

    public void regenMana(){
        mana = Math.min(mana + maxMana*0.05, maxMana);
        updateEntity();
    }

    public boolean reduceMana(int amount){
        if (mana >= amount){
            mana -= amount;
            updateEntity();
            return true;
        } else {
            return false;
        }
    }

    public boolean hasFullSet(){
        ItemStack[] armorContents = this.source.getInventory().getArmorContents();
        for (int i = 0; i < 3; i++){
            if (!((ArmorItem)ItemManager.convertItem(armorContents[i])).getSet().equals(((ArmorItem)ItemManager.convertItem(armorContents[i+1])).getSet())){
                return false;
            }
        }
        return true;
    }

    public FullSetBonus getFullSet(){
        ItemStack[] armorContents = this.source.getInventory().getArmorContents();
        if (armorContents[0] == null){
            return null;
        }
        String setName = ((ArmorItem)ItemManager.convertItem(armorContents[0])).getSetName();
        for (int i = 1; i < 4; i++){
            if (armorContents[i] == null || !setName.equals(((ArmorItem)ItemManager.convertItem(armorContents[i])).getSetName())){
                return null;
            }
        }
        if (FullSetBonusManager.validSet(setName)){
            return FullSetBonusManager.getSet(setName);
        } else {
            return null;
        }
    }

    public void giveItem(Item item){
        this.source.getInventory().addItem(item.getItemStack());
    }

    public void giveItem(String name){
        giveItem(new Item(name));
    }
}
