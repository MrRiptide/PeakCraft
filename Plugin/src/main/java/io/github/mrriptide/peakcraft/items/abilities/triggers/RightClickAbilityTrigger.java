package io.github.mrriptide.peakcraft.items.abilities.triggers;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Class to define a trigger in which the player has right clicked with an item to activate the item's ability
 */
public class RightClickAbilityTrigger extends AbilityTrigger{
    private Entity entity;
    private Player player;
    private EquipmentSlot hand;
    private Action action;
    private Block block;
    private BlockFace blockFace;

    /**
     * Instantiates an object using the information from a player interact entity event
     *
     * @param e The player interact entity event that triggered this ability
     */
    public RightClickAbilityTrigger(PlayerInteractEntityEvent e){
        this.player = e.getPlayer();
        this.hand = e.getHand();
        this.entity = e.getRightClicked();
    }

    /**
     * Instantiates an object using the information from a player interact entity event
     *
     * @param e The player interact entity event that triggered this ability
     */
    public RightClickAbilityTrigger(PlayerInteractEvent e){
        this.player = e.getPlayer();
        this.hand = e.getHand();
        this.action = e.getAction();
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR))
            throw new IllegalArgumentException("Cannot create a right-click ability trigger from a non-right click event");
        this.block = e.getClickedBlock();
        this.blockFace = e.getBlockFace();
    }

    public boolean hasEntity(){
        return entity != null;
    }

    public Entity getEntity() {
        return entity;
    }

    public Player getPlayer() {
        return player;
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public Action getAction() {
        return action;
    }
    public boolean hasAction(){
        return action != null;
    }

    public Block getBlock() {
        return block;
    }
    public boolean hasBlock(){
        return block != null;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }
    public boolean hasBlockFace(){
        return blockFace != null;
    }
}
