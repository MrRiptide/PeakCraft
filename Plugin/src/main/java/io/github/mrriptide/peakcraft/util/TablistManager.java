package io.github.mrriptide.peakcraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public abstract class TablistManager {

    public static void removePlayers(Player receiver) {
        Collection<? extends Player> playersBukkit = Bukkit.getOnlinePlayers();
        EntityPlayer[] playersNMS = new EntityPlayer[playersBukkit.size()];
        int current = 0;
        for (Player player : playersBukkit) {
            playersNMS[current] = ((CraftPlayer) player).getHandle();
            current++;
        }
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, playersNMS);
        ((CraftPlayer) receiver).getHandle().playerConnection.sendPacket(packet);
    }

    public static EntityPlayer createPlayer(String name, String listName, String texture, String signature) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldserver = server.getWorldServer(WorldServer.OVERWORLD);
        PlayerInteractManager playerinteractmanager = new PlayerInteractManager(worldserver);
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", new Property("textures", texture, signature));
        EntityPlayer player = new EntityPlayer(server, worldserver, profile, playerinteractmanager);
        player.listName = new ChatComponentText(listName);
        return player;
    }

    public static void addPlayers(Player receiver, EntityPlayer... createdPlayers) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, createdPlayers);
        ((CraftPlayer) receiver).getHandle().playerConnection.sendPacket(packet);
    }

    public static void fillBoard(Player receiver){
        EntityPlayer[] fakePlayers = new EntityPlayer[80];
        for (int i = 0; i < fakePlayers.length; i++){
            fakePlayers[i] = createPlayer(
                    " " + i/20 + i%20/2 + i%20%2,
                    "                ",
                    "eyJ0aW1lc3RhbXAiOjE1MjY2MTI4ODk4MjIsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U3NzYxODIxMjUyOTc5OGM3MTliNzE2MmE0NzNhNjg1YzQzNTczMjBhODY5NjE2NWU3OTY3OTBiOTBmYmE2NyJ9fX0=",
                    "KTnUMhshvKQ+4SYIxvHcu7kTrqw9It+v1oTjW7Xr3aGAGVwNH+z5PLCAY87UOytgoUUAI/mKAIAR8LzOb/x+z84v5lkVCr9pLV9zrzEZTC+jbzkwOMZ5nG1c4YMrh5p5sc7Abnm0dSE/3zLbH9Fev75HuTd9BgqqO1UjRM+sovuO+Gen4ZWRfliT+IFsALPO+3QCWoY2Cay9ZfPT4X7IJrX1GKfl0IiByVA9snyADH8LlwoNwAbe+v++1sy6G36xwrACdqQ9MLBMznpgUJbHlJmxuBsuioPymCAOaQUesRI3Yi053ZfABB+a7wU3tf9h0uNCUoWYr7w7e/N/3wDaphltH8A9MzDc9fFHjcen3+T4Ehl+0MKpY44eWTV6K22vQHuhO4h1c8ruvNTBlimTK27fc3uHhm9TL6ieXqf3UrSqA9bNqfnwHfFVdKXOMZ0cPPit7r6f3PmiVXteE+WijkN8PPzZtfEqU58jPKh3tAo4QzXYEgyGztY9NSGqCfvqBXMYIJgKgUPO3f5aUDmLwI2f1gZvWBsJ+VYHtMonBrIDg5U1bKsSzsQXNZZ+k55Zxe/1i8TEI4YsFGTYGco1UOd1KE+67XaQoPqAPyorNYhWeVmKSiGiHLhFt2RaE1mUf64pKTcyINyXmVlJKIMLIN4yvgAYFREAu/OA1GY6lt8="
            );
        }

        addPlayers(receiver, fakePlayers);
    }

}
