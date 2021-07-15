package io.github.mrriptide.peakcraft.commands;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class CommandMaterialList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        JsonFactory factory = new JsonFactory();
        try {
            JsonGenerator jsonGenerator = factory.createGenerator(new File(PeakCraft.getPlugin().getDataFolder() + "/materials.json"), JsonEncoding.UTF8);
            jsonGenerator.writeStartArray();
            for (org.bukkit.Material material : Material.values()){
                if (material.isItem()){
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("material_id", material.name());
                    jsonGenerator.writeEndObject();
                }
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.close();
            sender.sendMessage("Successfully generated materials.json");
            return true;
        } catch (IOException e){
            e.printStackTrace();
            sender.sendMessage("Something went wrong, please check the logs!");
            return false;
        }
    }
}