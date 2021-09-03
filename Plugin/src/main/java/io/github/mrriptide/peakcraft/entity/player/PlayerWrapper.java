package io.github.mrriptide.peakcraft.entity.player;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.CombatEntity;
import io.github.mrriptide.peakcraft.entity.npcs.NPC;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonus;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonusManager;
import io.github.mrriptide.peakcraft.runnables.UpdatePlayer;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.MySQLHelper;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class PlayerWrapper extends CombatEntity {
    protected Player source;
    protected double mana;
    protected double maxMana;
    protected double critChance;
    protected double critDamage;
    protected long lastDamageTime;
    protected double hunger;
    protected final double maxHunger = 500;
    protected long coins;
    protected PlayerStatus status;

    public PlayerWrapper(Player player){
        super("player", EntityType.SHEEP, ((CraftWorld) player.getWorld()).getHandle());
        this.source = player;
        this.maxHealth = 100;

        double intelligence = 0;

        for (ItemStack itemStack : player.getInventory().getArmorContents()){
            // IntelliJ will say that itemStack != null is always true, this is wrong.
            if (itemStack != null && itemStack.getType() != Material.AIR){
                try{
                    Item item = ItemManager.convertItem(itemStack);
                    if (item instanceof EnchantableItem){
                        ((EnchantableItem)item).bakeAttributes();
                        this.maxHealth += ((EnchantableItem)item).getBakedAttribute("health");
                        intelligence += ((EnchantableItem)item).getBakedAttribute("intelligence");
                    }
                } catch (ItemException e){
                    PeakCraft.getPlugin().getLogger().warning("Player " + player.getName() + " has an invalid armor item!");
                }

            }
        }

        this.maxMana = 100 + intelligence;

        this.health = Math.min(PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "health", maxHealth), maxHealth);
        this.mana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "mana", 0.0);
        this.hunger = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "hunger", 0.0);
        this.maxMana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "maxMana", 100.0);
        this.lastDamageTime = PersistentDataManager.getValueOrDefault(player, PersistentDataType.LONG, "lastDamageTime", Long.valueOf(0));
        this.critChance = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "critChance", 0.5);
        this.critDamage = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "critDamage", 0.5);
        this.name = player.getName();
        try{
            this.weapon = (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) ? ItemManager.convertItem(player.getInventory().getItemInMainHand()) : null;
        } catch (ItemException e) {
            PeakCraft.getPlugin().getLogger().warning("Player " + player.getName() + " has an invalid item in their hand!");
            this.weapon = null;
        }
        this.status = new PlayerStatus(player);

        try (Connection conn = PeakCraft.getDataSource().getConnection()){
            PreparedStatement statement = conn.prepareStatement("select coins from player_coins where uuid = ?;");
            statement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                coins = resultSet.getLong("coins");
            } else {
                coins = 0;
            }

            resultSet.close();
            statement.close();

        } catch (SQLException throwables) {
            PeakCraft.getPlugin().getLogger().warning("Failed to connect to the mysql database");
            throwables.printStackTrace();
        }

    }

    public void processDamage(double amount){
        lastDamageTime = (new Date()).getTime();
        super.processDamage(amount);
    }

    public void tryNaturalRegen(){
        if ((new Date()).getTime() - lastDamageTime > 5000){
            regenHealth(maxHealth / 100 * 5 / (20.0/UpdatePlayer.ticksPerUpdate));
        }
    }

    public void regenHealth(double amount){
        this.hunger -= Math.min(amount, maxHealth-health);

        super.regenHealth(amount);
    }

    public ArrayList<Item> getAbilityItems(){
        ArrayList<Item> abilityItems = new ArrayList<>();
        for (ItemStack itemStack : source.getInventory()){
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)){
                try{
                    Item item = ItemManager.convertItem(itemStack);
                    if (item.hasAbility()){
                        abilityItems.add(item);
                    }
                } catch (ItemException e) {
                    PeakCraft.getPlugin().getLogger().warning("Player " + source.getName() + " has an invalid item in their inventory!");
                }
            }
        }

        return abilityItems;
    }

    @Override
    public void initPathfinder() {
        // no need?
    }

    @Override
    public void updateEntity(){
        super.updateEntity();

        ((org.bukkit.entity.Player)this.getBukkitEntity()).setSaturation((float) Math.min(this.hunger/this.maxHunger*20.0, 20.0));
        PersistentDataManager.setValue(this.getBukkitEntity(), "mana", this.mana);
        PersistentDataManager.setValue(this.getBukkitEntity(), "hunger", this.mana);
        PersistentDataManager.setValue(this.getBukkitEntity(), "critChance", this.critChance);
        PersistentDataManager.setValue(this.getBukkitEntity(), "critDamage", this.critDamage);
        PersistentDataManager.setValue(this.getBukkitEntity(), "lastDamageTime", this.lastDamageTime);
        source.setLevel((int)mana);
        source.setExp((float) (mana / maxMana));
        sendActionBar();
    }

    @Override
    public CraftEntity getBukkitEntity() {
        return (CraftEntity) source;
    }

    @Override
    public void processAttack(CombatEntity attacker){
        // if attacked by an NPC or player, ignore
        if (!(attacker instanceof NPC || attacker instanceof PlayerWrapper)){
            super.processAttack(attacker);
        }
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
        mana = Math.min(mana + maxMana*0.05/(20.0/UpdatePlayer.ticksPerUpdate), maxMana);
        updateEntity();
    }

    public boolean reduceMana(int amount){
        if (source.getGameMode() == GameMode.CREATIVE){
            return true;
        }
        if (mana >= amount){
            mana -= amount;
            updateEntity();
            return true;
        } else {
            return false;
        }
    }

    public double getMana(){
        return mana;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public boolean hasFullSet(){
        ItemStack[] armorContents = this.source.getInventory().getArmorContents();
        for (int i = 0; i < 3; i++){
            try{
                if (!((ArmorItem)ItemManager.convertItem(armorContents[i])).getSet().equals(((ArmorItem)ItemManager.convertItem(armorContents[i+1])).getSet())){
                    return false;
                }
            } catch (ItemException e){
                PeakCraft.getPlugin().getLogger().warning("Player " + source.getName() + " has an invalid armor item equipped!");
            }
        }
        return true;
    }

    public FullSetBonus getFullSet(){
        ItemStack[] armorContents = this.source.getInventory().getArmorContents();
        if (armorContents[0] == null){
            return null;
        }
        try{
            String setName = ((ArmorItem)ItemManager.convertItem(armorContents[0])).getSetName();
            for (int i = 1; i < 4; i++){
                try{
                    if (armorContents[i] == null || !setName.equals(((ArmorItem)ItemManager.convertItem(armorContents[i])).getSetName())){
                        return null;
                    }
                } catch (ItemException e){
                    PeakCraft.getPlugin().getLogger().warning("Player " + source.getName() + " has an invalid armor item equipped!");
                    return null;
                }
            }
            if (FullSetBonusManager.validSet(setName)){
                return FullSetBonusManager.getSet(setName);
            } else {
                return null;
            }
        } catch (ItemException e){
            PeakCraft.getPlugin().getLogger().warning("Player " + source.getName() + " has an invalid armor item equipped!");
            return null;
        }
    }

    public double getCritChance() {
        return critChance;
    }

    public double getCritDamage() {
        return critDamage;
    }

    public Player getSource() {
        return source;
    }

    public long getCoins() {
        return coins;
    }


    public void giveItem(Item item){
        this.source.getInventory().addItem(item.getItemStack());
    }

    public void giveItem(String name){
        giveItem(new Item(name));
    }

    public void saveInventory() {
        try (Connection conn = PeakCraft.getDataSource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM player_inventories WHERE uuid = ?");
            statement.setString(1, getSource().getUniqueId().toString());

            statement.execute();
            statement.close();

            ArrayList<Object[]> values = new ArrayList<>();

            for (int i = 0; i < getSource().getInventory().getSize(); i++){
                if (getSource() != null && getSource().getInventory().getItem(i) != null && getSource().getInventory().getItem(i).getType() != Material.AIR){
                    try {
                        io.github.mrriptide.peakcraft.items.ItemStack itemStack = new io.github.mrriptide.peakcraft.items.ItemStack(getSource().getInventory().getItem(i));
                        Object[] objectSet = new Object[5];

                        // uuid
                        objectSet[0] = getSource().getUniqueId().toString();
                        // item index
                        objectSet[2] = i;
                        // item id
                        objectSet[1] = itemStack.getItem().getId();
                        // item count
                        objectSet[3] = itemStack.getAmount();
                        // item nbt
                        objectSet[4] = "";

                        values.add(objectSet);
                    } catch (ItemException e) {
                        e.printStackTrace();
                    }
                }
            }
            MySQLHelper.bulkInsert(conn, "INSERT INTO player_inventories(uuid, item_id, item_index, item_count, nbt) VALUES", values);
            Bukkit.broadcastMessage("saved inventory");
        } catch (SQLException e){
            PeakCraft.getPlugin().getLogger().warning("Something went wrong while saving player " + getSource().getUniqueId().toString() + "'s inventory to the mysql database");
            e.printStackTrace();
        }
    }

    public class PlayerStatus{

        private boolean flightAllowed;
        private boolean flying;

        public PlayerStatus(Player player){
            flightAllowed = player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
            flying = player.isFlying();
        }

        public void init(){
            flightAllowed = source.getGameMode() == GameMode.CREATIVE || source.getGameMode() == GameMode.SPECTATOR;
            flying = source.isFlying();
        }

        public void apply(){
            source.setAllowFlight(flightAllowed);
            source.setFlying(flightAllowed && flying);
        }

        public void setFlightAllowed(boolean flightAllowed){
            this.flightAllowed = source.getGameMode() == GameMode.CREATIVE || source.getGameMode() == GameMode.SPECTATOR || flightAllowed;
        }

        public void setFlying(boolean flying){
            this.flying = flying;
        }
        public boolean isFlightAllowed() {
            return flightAllowed;
        }

        public boolean isFlying() {
            return flying;
        }
    }
}
