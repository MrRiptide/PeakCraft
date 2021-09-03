package io.github.mrriptide.peakcraft.guis;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.ItemStack;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.MySQLHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VaultGUI implements InventoryGui{
    PlayerWrapper player;
    List<HashMap<Integer, ItemStack>> vaults;
    private int currentVault;

    public VaultGUI(Player player){
        this.player = PlayerManager.getPlayer(player);

        // load items from the player's vault

        try(Connection conn = PeakCraft.getDataSource().getConnection()){
            PreparedStatement stmt = conn.prepareStatement("select vaults from player_upgrades WHERE uuid = ?");
            stmt.setString(1, player.getUniqueId().toString());

            int vault_count = 1;

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                vault_count = resultSet.getInt("vault_count");
            }

            vaults = new ArrayList<>();

            for (int i = 0; i < vault_count; i++){
                vaults.add(new HashMap<>());
            }

            resultSet.close();
            stmt.close();

            stmt = conn.prepareStatement("select * from player_vaults WHERE uuid = ?");
            stmt.setString(1, player.getUniqueId().toString());

            int failed_items = 0;
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                try{
                    vaults.get(resultSet.getInt("vault_index")).put(resultSet.getInt("item_index"), new ItemStack(ItemManager.getItem(resultSet.getString("item_id")), resultSet.getInt("item_count")));
                } catch (ItemException e){
                    failed_items++;
                    PeakCraft.getPlugin().getLogger().warning("Player " + player.getUniqueId() + " has an invalid item in index " + resultSet.getInt("item_index") + " of vault " + resultSet.getInt("vault_index"));
                }
            }

            if (failed_items > 0){
                player.sendMessage(CustomColors.ERROR + "Failed loading " + failed_items + " items in your vaults, please let staff know!");
                vaults = null;
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean failedLoading(){
        return vaults == null;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, player.getSource().getName() + "'s Vault");
        loadPage(0, inventory);
        return inventory;
    }

    @Override
    public boolean onGUIClick(Player player, int slot, InventoryClickEvent e) {
        if (slot >= 44 && slot <= 54){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent e){
        saveCurrentPage(e.getInventory());
        saveData();
    }

    private void saveCurrentPage(Inventory inventory){
        HashMap<Integer, ItemStack> vault = new HashMap<>();

        int failedItems = 0;
        for (int i = 0; i < inventory.getSize() - 9; i++){
            org.bukkit.inventory.ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null && itemStack.getType() != Material.AIR){
                try{
                    vault.put(i, new ItemStack(itemStack));
                } catch (ItemException e) {
                    failedItems++;
                    e.printStackTrace();
                }
            }
        }
        if (failedItems >0){
            player.getSource().sendMessage(CustomColors.ERROR + "" + failedItems + " items failed to save and have been sent to your deliveries, please report this to the admins!");
        }
        vaults.set(currentVault, vault);
    }

    public void saveData() {
        try (Connection conn = PeakCraft.getDataSource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM player_vaults WHERE uuid = ?");
            statement.setString(1, player.getSource().getUniqueId().toString());

            statement.execute();
            statement.close();

            ArrayList<Object[]> values = new ArrayList<>();

            for (int i = 0; i < vaults.size(); i++){
                for (Map.Entry<Integer, ItemStack> entry : vaults.get(i).entrySet()){
                    Object[] valueSet = new Object[6];
                    // uuid
                    valueSet[0] = player.getSource().getUniqueId().toString();
                    // item_id
                    valueSet[1] = entry.getValue().getItem().getId();
                    // vault_index
                    valueSet[2] = i;
                    // item_index
                    valueSet[3] = entry.getKey();
                    // item_count
                    valueSet[4] = entry.getValue().getAmount();
                    // nbt
                    valueSet[5] = "";

                    values.add(valueSet);
                }
            }
            MySQLHelper.bulkInsert(conn, "INSERT INTO player_vaults(uuid, item_id, vault_index, item_index, item_count, nbt) VALUES ", values);
            Bukkit.broadcastMessage("saved vaults");
        } catch (SQLException e){
            PeakCraft.getPlugin().getLogger().warning("Something went wrong while saving player " + player.getSource().getUniqueId().toString() + "'s vault to the mysql database");
            e.printStackTrace();
        }

        player.saveInventory();
    }

    private void loadPage(int page, Inventory inventory){
        currentVault = page;

        // Create background item
        org.bukkit.inventory.ItemStack background_item = new org.bukkit.inventory.ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta background_item_meta = background_item.getItemMeta();
        assert background_item_meta != null;
        background_item_meta.setDisplayName(" ");
        background_item.setItemMeta(background_item_meta);
        for (int i = 45; i < 54; i++){
            inventory.setItem(i, background_item);
        }

        if (page > 0){
            org.bukkit.inventory.ItemStack leftArrowItem = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) leftArrowItem.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft"));
            meta.setDisplayName(ChatColor.WHITE + "Page left (" + page+1 + "/" + vaults.size() + ")");
            leftArrowItem.setItemMeta(meta);

            inventory.setItem(45, leftArrowItem);
        }
        if (page < vaults.size()-1){
            org.bukkit.inventory.ItemStack rightArrowItem = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) rightArrowItem.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowRight"));
            meta.setDisplayName(ChatColor.WHITE + "Page right (" + page+1 + "/" + vaults.size() + ")");
            rightArrowItem.setItemMeta(meta);

            inventory.setItem(53, rightArrowItem);
        }

        for (Map.Entry<Integer, ItemStack> item :  vaults.get(page).entrySet()){
            if (item.getKey() < 45){
                inventory.setItem(item.getKey(), item.getValue().toBukkit());
            }
        }
    }
}
