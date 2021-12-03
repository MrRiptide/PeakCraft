package io.github.mrriptide.peakcraft.guis;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.recipes.ShapedRecipe;
import io.github.mrriptide.peakcraft.recipes.RecipeItem;
import io.github.mrriptide.peakcraft.recipes.RecipeManager;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class EditRecipeGUI implements InventoryGui{
    private String recipeName;
    private final int saveSlot = 16;
    private final int cancelSlot = 34;
    private final int[] openSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30, 23};

    public EditRecipeGUI(String recipeName){
        this.recipeName = recipeName;
    }

    @Override
    public Inventory getInventory() {
        // Get the inventory to use as the gui

        Inventory gui = Bukkit.createInventory(this, 45, "Create Recipe");

        // Create background item
        ItemStack background_item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta background_item_meta = background_item.getItemMeta();
        assert background_item_meta != null;
        background_item_meta.setDisplayName(" ");
        background_item.setItemMeta(background_item_meta);

        for (int i = 0; i < 45; i++){
            gui.setItem(i, background_item);
        }

        // If the recipe already exists, load current one
        if (RecipeManager.recipeExists(this.recipeName)){
            ShapedRecipe recipe = (ShapedRecipe) RecipeManager.getRecipe(this.recipeName);
            assert recipe != null;
            HashMap<Character, RecipeItem> ingredientMap = recipe.getIngredients();
            String[] shape = recipe.getShape();
            for (int i = 0; i < 3; i++){
                for (int j = 0; j < 3; j++){
                    if (ingredientMap.get(shape[i].charAt(j)) != null){
                        //gui.setItem(9*(i+1) + j + 1, ingredientMap.get(shape[i].charAt(j)).getItemStack());
                    }
                    else {
                        gui.setItem(9*(i+1) + j + 1, new ItemStack(Material.AIR));
                    }
                }
            }

            gui.setItem(23, recipe.getResult());
        } else {
            ItemStack air = new ItemStack(Material.AIR);

            // set open slots to air

            for (int slot : openSlots){
                gui.setItem(slot, air);
            }
        }

        // Create confirm button
        ItemStack save = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta save_meta = save.getItemMeta();
        save_meta.setDisplayName(CustomColors.YES + "Save Recipe");
        save.setItemMeta(save_meta);

        // Place save button
        gui.setItem(saveSlot, save);

        // Create cancel button
        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancel_meta = cancel.getItemMeta();
        cancel_meta.setDisplayName(CustomColors.NO + "Cancel");
        cancel.setItemMeta(cancel_meta);

        // Place cancel button
        gui.setItem(cancelSlot, cancel);

        return gui;
    }

    @Override
    public boolean onGUIClick(Player player, int slot, InventoryClickEvent e) {

        // if is the cancel button, close the menu
        if (slot == cancelSlot){
            player.closeInventory();
            return true;
        }

        // if is the save button, create the new version of the recipe and save it
        if (slot == saveSlot){
            Inventory inventory = e.getClickedInventory();

            RecipeItem[][] ingredients = new RecipeItem[3][3];
            assert inventory != null;
            for (int i = 0; i < 3; i++){
                for (int j = 0; j < 3; j++){
                    ItemStack itemStack = inventory.getItem(10 + i * 9 + j);
                    if (itemStack != null) {
                        try {
                            ingredients[i][j] = new RecipeItem(itemStack);
                        } catch (ItemException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        try {
                            ingredients[i][j] = new RecipeItem("AIR");
                        } catch (ItemException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            RecipeItem result = null;
            try {
                result = new RecipeItem(Objects.requireNonNull(inventory.getItem(23)));
            } catch (ItemException ex) {
                ex.printStackTrace();
            }
            ShapedRecipe recipe = new ShapedRecipe(ingredients, result);
            if (inventory.getItem(23) == null){
                player.sendMessage("Must have a item defined as the result");
                return true;
            }

            RecipeManager.saveRecipe(recipe, recipeName);
            RecipeManager.registerRecipe(recipeName);

            player.closeInventory();

            return true;
        }

        // if one of the open slots, return false to not cancel
        if (Arrays.stream(openSlots).anyMatch(x -> x == slot)){
            return false;
        }

        // if outside of the custom gui inventory part, return false to not cancel
        if (slot >= 45){
            return false;
        }

        // if not one of the designated slots, return true to cancel
        return true;
    }
}
