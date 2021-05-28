package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.runnables.UpdatePlayer;
import io.github.mrriptide.peakcraft.util.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Locale;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Bukkit.getScheduler().runTaskLater(PeakCraft.instance, () -> {
            TablistManager.removePlayers(e.getPlayer());

            TablistManager.fillBoard(e.getPlayer());
        }, 10);

        BukkitTask task = new UpdatePlayer(e.getPlayer()).runTaskTimer(PeakCraft.instance, 0, 10);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getItem() != null){
                Item item = ItemManager.convertItem(e.getItem());

                if (item instanceof ArmorItem){
                    ArrayList<String> types = new ArrayList<>();
                    types.add("helmet");
                    types.add("chestplate");
                    types.add("leggings");
                    types.add("boots");

                    int slot = types.indexOf(item.getType().toLowerCase(Locale.ROOT));

                    ItemStack[] armorContents = e.getPlayer().getInventory().getArmorContents();
                    if (slot >= 0 && armorContents[3-slot] == null){
                        armorContents[3-slot] = e.getItem();
                        e.getPlayer().getInventory().setArmorContents(armorContents);
                        if (e.getHand() == EquipmentSlot.HAND){
                            e.getPlayer().getInventory().setItemInMainHand(null);
                        } else if (e.getHand() == EquipmentSlot.OFF_HAND){
                            e.getPlayer().getInventory().setItemInOffHand(null);
                        }
                    } else {
                        for (int i = 0; i < 4; i++){
                            if (armorContents[i] == null){
                                armorContents[3-i] = e.getItem();
                                e.getPlayer().getInventory().setArmorContents(armorContents);
                                if (e.getHand() == EquipmentSlot.HAND){
                                    e.getPlayer().getInventory().setItemInMainHand(null);
                                } else if (e.getHand() == EquipmentSlot.OFF_HAND){
                                    e.getPlayer().getInventory().setItemInOffHand(null);
                                }
                                break;
                            }
                        }
                    }
                    e.setCancelled(true);
                }
                if (item.hasAbility()){
                    PlayerWrapper player = new PlayerWrapper(e.getPlayer());
                    item.useAbility(player);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onArmorEquip(InventoryClickEvent e){
        //if (e.getSlotType() == InventoryType.SlotType.ARMOR)
    }
}
