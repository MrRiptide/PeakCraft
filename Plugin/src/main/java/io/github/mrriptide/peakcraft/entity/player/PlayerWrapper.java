package io.github.mrriptide.peakcraft.entity.player;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.wrappers.CombatEntityWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonus;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonusManager;
import io.github.mrriptide.peakcraft.runnables.UpdatePlayer;
import io.github.mrriptide.peakcraft.util.Attribute;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.MySQLHelper;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class PlayerWrapper extends CombatEntityWrapper {
    protected double mana;
    protected Attribute maxMana;
    protected Attribute critChance;
    protected Attribute critDamage;
    protected long lastDamageTime;
    protected double hunger;
    protected final double maxHunger = 500;
    protected long coins;
    protected PlayerStatus status;

    public PlayerWrapper(Player player) throws EntityException {
        super();
        this.entity = player;
        this.id = "player";
        this.maxHealth = new Attribute(100);
        this.defense = new Attribute(0);
        this.strength = new Attribute(0);

        this.health = Math.min(PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "health", maxHealth.getFinal()), maxHealth.getFinal());
        this.mana = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "mana", 0.0);
        this.hunger = PersistentDataManager.getValueOrDefault(player, PersistentDataType.DOUBLE, "hunger", 0.0);
        this.maxMana = PersistentDataManager.getAttribute(player, "maxMana", 100.0);
        this.lastDamageTime = PersistentDataManager.getValueOrDefault(player, PersistentDataType.LONG, "lastDamageTime", Long.valueOf(0));
        this.critChance = PersistentDataManager.getAttribute(player, "critChance", 0.5);
        this.critDamage = PersistentDataManager.getAttribute(player, "critDamage", 0.5);
        this.name = player.getName();
        updateAttributes();

        this.status = new PlayerStatus(player);

        try (Connection conn = MySQLHelper.getConnection()){
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

    @Override
    public void resetAttributes(){
        super.resetAttributes();
        this.maxMana.reset();
        this.critChance.reset();
        this.critDamage.reset();
    }

    @Override
    public void processItem(EnchantableItem item){
        super.processItem(item);
        this.maxMana.addAdditive(item.getAttribute("intelligence").getFinal());
    }

    public void processDamage(double amount){
        lastDamageTime = (new Date()).getTime();
        super.processDamage(amount);
    }

    public void tryNaturalRegen(){
        if ((new Date()).getTime() - lastDamageTime > 5000){
            regenHealth(maxHealth.getFinal() / 100 * 5 / (20.0/UpdatePlayer.ticksPerUpdate));
        }
    }

    public void regenHealth(double amount){
        this.hunger -= Math.min(amount, maxHealth.getFinal()-health);

        super.regenHealth(amount);
    }

    public ArrayList<Item> getAbilityItems(){
        ArrayList<Item> abilityItems = new ArrayList<>();
        for (ItemStack itemStack : ((Player)entity).getInventory()){
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)){
                try{
                    Item item = ItemManager.convertItem(itemStack);
                    if (item.hasAbility()){
                        abilityItems.add(item);
                    }
                } catch (ItemException e) {
                    PeakCraft.getPlugin().getLogger().warning("Player " + entity.getName() + " has an invalid item in their inventory!");
                }
            }
        }

        return abilityItems;
    }

    @Override
    public void updateEntity(){
        super.updateEntity();

        ((org.bukkit.entity.Player)entity).setSaturation((float) Math.min(this.hunger/this.maxHunger*20.0, 20.0));
        PersistentDataManager.setValue(entity, "mana", this.mana);
        PersistentDataManager.setValue(entity, "hunger", this.mana);
        PersistentDataManager.setAttribute(entity, "critChance", this.critChance);
        PersistentDataManager.setAttribute(entity, "critDamage", this.critDamage);
        PersistentDataManager.setValue(entity, "lastDamageTime", this.lastDamageTime);
        /*
        temporary removal of using experience bar to show mana, I want to let people have experience racked up now
        ((Player)entity).setLevel((int)mana);
        ((Player)entity).setExp((float) (mana / maxMana));*/
        sendActionBar();
    }

    @Override
    public void processAttack(CombatEntityWrapper attacker){
        // if attacked by an NPC or player, ignore
        if (!(attacker instanceof PlayerWrapper)){
            super.processAttack(attacker);
        }
    }

    public void resetStats(){
        this.health = maxHealth.getFinal();
        this.mana = maxMana.getFinal();
        updateEntity();
    }

    public void sendActionBar(){
        ((Player)this.entity).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CustomColors.HEALTH + "" + (int)this.health + "/" + (int)this.maxHealth.getFinal() + "❤            " + CustomColors.MANA + (int)mana +  "/" + (int)maxMana.getFinal() + "MP"));
    }

    public void regenMana(){
        mana = Math.min(mana + maxMana.getFinal()*0.05/(20.0/UpdatePlayer.ticksPerUpdate), maxMana.getFinal());
        updateEntity();
    }

    public boolean reduceMana(int amount){
        if (((Player)entity).getGameMode() == GameMode.CREATIVE){
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
        ItemStack[] armorContents = ((Player)entity).getInventory().getArmorContents();
        for (int i = 0; i < 3; i++){
            try{
                if (!((ArmorItem)ItemManager.convertItem(armorContents[i])).getSet().equals(((ArmorItem)ItemManager.convertItem(armorContents[i+1])).getSet())){
                    return false;
                }
            } catch (ItemException e){
                PeakCraft.getPlugin().getLogger().warning("Player " + entity.getName() + " has an invalid armor item equipped!");
            }
        }
        return true;
    }

    public FullSetBonus getFullSet(){
        ItemStack[] armorContents = ((Player)entity).getInventory().getArmorContents();
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
                    PeakCraft.getPlugin().getLogger().warning("Player " + entity.getName() + " has an invalid armor item equipped!");
                    return null;
                }
            }
            if (FullSetBonusManager.validSet(setName)){
                return FullSetBonusManager.getSet(setName);
            } else {
                return null;
            }
        } catch (ItemException e){
            PeakCraft.getPlugin().getLogger().warning("Player " + entity.getName() + " has an invalid armor item equipped!");
            return null;
        }
    }

    public double getCritChance() {
        return critChance.getFinal();
    }

    public double getCritDamage() {
        return critDamage.getFinal();
    }

    public Player getSource() {
        return ((Player)entity);
    }

    public long getCoins() {
        return coins;
    }


    public void giveItem(Item item){
        ((Player)entity).getInventory().addItem(item.getItemStack());
    }

    public void giveItem(String name){
        giveItem(new Item(name));
    }

    public void saveInventory() {
        try (Connection conn = MySQLHelper.getConnection()) {
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
            flightAllowed = ((Player)entity).getGameMode() == GameMode.CREATIVE || ((Player)entity).getGameMode() == GameMode.SPECTATOR;
            flying = ((Player)entity).isFlying();
        }

        public void apply(){
            ((Player)entity).setAllowFlight(flightAllowed);
            ((Player)entity).setFlying(flightAllowed && flying);
        }

        public void setFlightAllowed(boolean flightAllowed){
            this.flightAllowed = ((Player)entity).getGameMode() == GameMode.CREATIVE || ((Player)entity).getGameMode() == GameMode.SPECTATOR || flightAllowed;
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
