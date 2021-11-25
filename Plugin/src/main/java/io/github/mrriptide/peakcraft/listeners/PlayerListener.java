package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.abilities.triggers.RightClickAbilityTrigger;
import io.github.mrriptide.peakcraft.runnables.UpdatePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class PlayerListener implements Listener {
    private HashMap<UUID, Long> lastInteractTime = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        /*Bukkit.getScheduler().runTaskLater(PeakCraft.instance, () -> {
            Tablist tablist = new Tablist(new PlayerWrapper());
            Tablist.removePlayers(e.getPlayer());

            Tablist.fillBoard(e.getPlayer());
        }, 10);*/

        PlayerManager.logInPlayer(e.getPlayer());
        BukkitTask task = new UpdatePlayer(e.getPlayer()).runTaskTimer(PeakCraft.instance, 0, UpdatePlayer.ticksPerUpdate);
    }

    private boolean isOffCooldown(PlayerEvent e) {
        if (System.nanoTime() - lastInteractTime.getOrDefault(e.getPlayer().getUniqueId(), (long) 0) < 50000000){
            ((e instanceof PlayerInteractEntityEvent) ? (PlayerInteractEntityEvent)e : (PlayerInteractEvent)e).setCancelled(true);
            return false;
        } else {
            lastInteractTime.put(e.getPlayer().getUniqueId(), System.nanoTime());
            return true;
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        try {
            PlayerWrapper playerWrapper = new PlayerWrapper(e.getPlayer());
            playerWrapper.resetStats();
        } catch (EntityException ex) {
            PeakCraft.getPlugin().getLogger().warning("Player respawned but could not be wrapped!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if (!isOffCooldown(e)){
            return;
        }
        if (e.getItem() != null){
            try{
                Item item = ItemManager.convertItem(e.getItem());
                if (item.hasAbility()){
                    PlayerWrapper player = null;
                    try {
                        player = new PlayerWrapper(e.getPlayer());
                    } catch (EntityException ex) {
                        PeakCraft.getPlugin().getLogger().warning("Player interacted but could not be wrapped");
                        ex.printStackTrace();
                        return;
                    }
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
                        item.useAbility(player, new RightClickAbilityTrigger(e));
                    }
                    e.setCancelled(true);
                }

                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
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
                }
            } catch (ItemException error){
                e.getPlayer().sendMessage("That item has an invalid id, please report this!");
                PeakCraft.getPlugin().getLogger().warning("Player " + e.getPlayer().getName() + " interacted with an invalid item!");
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
        if (!isOffCooldown(e)){
            return;
        }
        if (e.getPlayer().getEquipment() != null){
            try{
                Item item = ItemManager.convertItem(e.getPlayer().getEquipment().getItem(e.getHand()));

                if (item.hasAbility()) {
                    PlayerWrapper player = new PlayerWrapper(e.getPlayer());
                    item.useAbility(player, new RightClickAbilityTrigger(e));
                    e.setCancelled(true);
                }
            } catch (ItemException error){
                e.getPlayer().sendMessage("That item has an invalid id, please report this!");
                PeakCraft.getPlugin().getLogger().warning("Player " + e.getPlayer().getName() + " interacted with an invalid item!");
            } catch (EntityException entityException) {
                PeakCraft.getPlugin().getLogger().warning("A player interacted but could not be wrapped");
                entityException.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onArmorEquip(InventoryClickEvent e){
        //if (e.getSlotType() == InventoryType.SlotType.ARMOR)
    }
}
