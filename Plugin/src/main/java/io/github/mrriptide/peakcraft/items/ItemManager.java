package io.github.mrriptide.peakcraft.items;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import io.github.mrriptide.peakcraft.PeakCraft;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class ItemManager {

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

    public static void loadItems() {
        items = new HashMap<String, Item>();

        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setHeaderExtractionEnabled(true);

        TsvParser parser = new TsvParser(settings);


        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(PeakCraft.getPlugin().getDataFolder() + File.separator + "items.tsv"));
        } catch (FileNotFoundException e) {
            PeakCraft.getPlugin().getLogger().log(Level.SEVERE, "File \"items.tsv\" not found, generating one now");
            createItemList();
            try {
                file = new FileInputStream(new File(PeakCraft.getPlugin().getDataFolder() + File.separator + "items.tsv"));
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }

        ArrayList<Record> allRecords = (ArrayList<Record>) parser.parseAllRecords(file);

        for (Record record : allRecords){
            int i = 7;
            HashMap<String, Integer> attributes = new HashMap<>();
            while (record.getString(i) != null){
                attributes.put(record.getString(i).toLowerCase(), record.getInt(i+1));
                i+=2;
            }
            Item item = new Item(
                    record.getString("ID"), // the id
                    record.getString("Ore Dictionary"), // The display name
                    record.getString("Display Name"), // The display name
                    record.getValue("Rarity", 0), // the rarity (as an int)
                    record.getString("Description"), // the description
                    Material.getMaterial(record.getString("Material")),// the material
                    record.getString("Item Type"),// the type to be displayed
                    attributes
            );
            items.put(item.getID(), item);
        }

        PeakCraft.getPlugin().getLogger().info("Successfully loaded " + items.size() + " items.");
    }

    public static Item getItem(String id){
        return items.get(id.toUpperCase()).clone();
    }

    private static void createItemList() {
        ArrayList<String[]> output_data = new ArrayList<>();

        TsvWriterSettings settings = new TsvWriterSettings();
        settings.getFormat().setLineSeparator("\n");

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(new File(PeakCraft.getPlugin().getDataFolder() + File.separator + "items.tsv"));
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
            } else {
                PeakCraft.getPlugin().getLogger().info(mat.name() + " is not an item");
            }
        }
        writer.close();
    }
}
