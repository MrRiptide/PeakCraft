package io.github.mrriptide.peakcraft.items;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.recipes.ShapedRecipe;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class ItemManager {

    private static String itemFilePath = "items.json";
    private static HashMap<String, Item> items;

    public static ItemStack ConvertItem(ItemStack item){
        NamespacedKey key = new NamespacedKey(PeakCraft.instance, "ITEM_ID");
        ItemMeta origMeta = item.getItemMeta();
        String item_id = item.getType().name();
        if (origMeta != null){
            PersistentDataContainer container = origMeta.getPersistentDataContainer();
            if (container.has(key, PersistentDataType.STRING)){
                item_id = container.get(key, PersistentDataType.STRING);
            }
        }

        assert item_id != null;
        Item item_data = getItem(item_id);

        return item_data.convertItem(item);
    }

    public static void getItemFromItemStack(ItemStack itemStack){
        Item item;

        String type = PersistentDataManager.getValueOrDefault(itemStack, PersistentDataType.STRING, "type", "item");
    }

    public static void loadItems() {
        items = new HashMap<String, Item>();

        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setHeaderExtractionEnabled(true);

        TsvParser parser = new TsvParser(settings);


        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(PeakCraft.getPlugin().getDataFolder() + File.separator + itemFilePath));
        } catch (FileNotFoundException e) {
            PeakCraft.getPlugin().getLogger().log(Level.SEVERE, "File \"" + itemFilePath + "\" not found, generating one now");
            createItemList();

            try {
                file = new FileInputStream(new File(PeakCraft.getPlugin().getDataFolder() + File.separator + itemFilePath));
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            HashMap<String, HashMap<String, String>> itemsSource = objectMapper.readValue(file, new TypeReference<HashMap<String, HashMap<String, String>>>(){});

            for (HashMap<String, String> itemData : itemsSource.values()){

                Item item;

                String type = itemData.get("type").toLowerCase(Locale.ROOT);
                if ((Arrays.asList(new String[]{"armor", "chestplate", "helmet", "leggings", "boots"})).contains(type)) {
                    item = ArmorItem.loadFromHashMap(itemData);
                } else if ((Arrays.asList(new String[]{"weapon", "sword"})).contains(type)) {
                    item = WeaponItem.loadFromHashMap(itemData);
                } else {
                    item = Item.loadFromHashMap(itemData);
                }

                items.put(item.getId(), item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PeakCraft.getPlugin().getLogger().info("Successfully loaded " + items.size() + " items.");
    }

    public static Item getItem(String id){
        return items.get(id.toUpperCase()).clone();
    }

    private static void createItemList() {
        try {

            if (!PeakCraft.instance.getDataFolder().exists()){
                PeakCraft.instance.getDataFolder().mkdirs();
            }

            HashMap<String, HashMap<String, String>> items = new HashMap<>();

            for (Material mat : Material.values()){
                if (mat.isItem()){
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", mat.name());
                    map.put("oreDict", "");
                    map.put("description", "");
                    map.put("displayName", WordUtils.capitalizeFully(mat.toString().toLowerCase().replace("_", " ")));
                    map.put("rarity", "1");
                    map.put("materialID", mat.name());
                    map.put("type", "Item");

                    items.put(mat.name(), map);
                } else {
                    PeakCraft.getPlugin().getLogger().info(mat.name() + " is not an item");
                }
            }

            // save using jackson https://stackabuse.com/reading-and-writing-json-in-java/

            File recipeFile = new File(PeakCraft.instance.getDataFolder() + File.separator + itemFilePath);

            OutputStream outputStream = new FileOutputStream(recipeFile);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(outputStream, items);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
