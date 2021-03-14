package io.github.mrriptide.peakcraft;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Level;

public class ItemManager {

    private static final String itemsPath = "plugins/config/PeakCraft/items.tsv";

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

        Item item_data = getItem(item_id);

        return item_data.convertItem(item);
    }

    public static void LoadItems() {
        items = new HashMap<String, Item>();

        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setHeaderExtractionEnabled(true);

        TsvParser parser = new TsvParser(settings);


        FileInputStream file = null;
        try {
            file = new FileInputStream(itemsPath);
        } catch (FileNotFoundException e) {
            PeakCraft.getPlugin().getLogger().log(Level.SEVERE, "File \"items.tsv\" not found, generating one now");
            createItemList();
            try {
                file = new FileInputStream(itemsPath);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }

        ArrayList<Record> allRecords = (ArrayList<Record>) parser.parseAllRecords(file);

        for (Record record : allRecords){
            Item item = new Item(
                    record.getString("ID"), // the id
                    record.getString("Display Name"), // The display name
                    record.getValue("Rarity", 0), // the rarity (as an int)
                    record.getString("Description"), // the description
                    Material.getMaterial(record.getString("Material")));// the material
            items.put(item.getID(), item);
        }

        PeakCraft.getPlugin().getLogger().info("Successfully loaded " + items.size() + " items.");
    }

    public static Item getItem(String id){
        return items.get(id.toUpperCase());
    }

    private static void createItemList() {
        ArrayList<String[]> output_data = new ArrayList<String[]>();

        TsvWriterSettings settings = new TsvWriterSettings();
        settings.getFormat().setLineSeparator("\n");

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(itemsPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert output != null;
        TsvWriter writer = new TsvWriter(output, settings);

        writer.writeHeaders("ID", "Display Name", "Rarity", "Description", "Material");

        for (Material mat : Material.values()){
            if (mat.isItem()){
                String[] line = new String[]{mat.name(), WordUtils.capitalizeFully(mat.toString().toLowerCase().replace("_", " ")), "0", "", mat.name()};
                writer.writeRow(line);
            }
        }
        writer.close();
    }
}
