package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.enchantments.Enchantment;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentHealthBoost;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentSharpness;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandTest implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            HashMap<String, Enchantment> enchantments = EnchantmentManager.getEnchantments();

            HashMap<String, Class<? extends Enchantment>> enchantmentClasses = new HashMap<>();
            enchantmentClasses.put("sharpness", EnchantmentSharpness.class);
            enchantmentClasses.put("health_boost", EnchantmentHealthBoost.class);

            EnchantableItem item = (EnchantableItem) ItemManager.getItem("potato_sword");
            item.bakeAttributes();

            int repeatTimes = Integer.parseInt(args[0]);

            long startTime = System.nanoTime();
            for (int i = 0; i < repeatTimes; i++){
                for (Map.Entry<String, Enchantment> enchantment : enchantments.entrySet()){
                    enchantment.getValue().bakeItemAttributes(item, 3);
                }
            }
            double timeTaken = (System.nanoTime() - startTime)/ 1000000000.0;
            commandSender.sendMessage("It took " + ((int)(100*timeTaken))/100.0 + " seconds to process the " + enchantments.size() + " enchants " + args[0] + " times from objects");

            startTime = System.nanoTime();
            for (int i = 0; i < repeatTimes; i++){
                for (Map.Entry<String, Class<? extends Enchantment>> enchantment : enchantmentClasses.entrySet()){

                    try {
                        enchantment.getValue().newInstance().bakeItemAttributes(item, 3);
                    } catch (IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
            timeTaken = (System.nanoTime() - startTime)/ 1000000000.0;
            commandSender.sendMessage("It took " + ((int)(100*timeTaken))/100.0 + " seconds to process the " + enchantments.size() + " enchants " + args[0] + " times from classes");
        }

        return true;
    }
}
