package io.github.mrriptide.peakcraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class Tablist {
    private HashMap<String, ServerPlayer> updates;
    private PlayerWrapper receiver;
    private final int boardSize = 80;
    private final int lineWidth = 30;
    private HashMap<String, ServerPlayer> currentPlayers;
    private String blankTextureValue = "eyJ0aW1lc3RhbXAiOjE1MjY2MTI4ODk4MjIsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U3NzYxODIxMjUyOTc5OGM3MTliNzE2MmE0NzNhNjg1YzQzNTczMjBhODY5NjE2NWU3OTY3OTBiOTBmYmE2NyJ9fX0=";
    private String blankTextureSignature = "KTnUMhshvKQ+4SYIxvHcu7kTrqw9It+v1oTjW7Xr3aGAGVwNH+z5PLCAY87UOytgoUUAI/mKAIAR8LzOb/x+z84v5lkVCr9pLV9zrzEZTC+jbzkwOMZ5nG1c4YMrh5p5sc7Abnm0dSE/3zLbH9Fev75HuTd9BgqqO1UjRM+sovuO+Gen4ZWRfliT+IFsALPO+3QCWoY2Cay9ZfPT4X7IJrX1GKfl0IiByVA9snyADH8LlwoNwAbe+v++1sy6G36xwrACdqQ9MLBMznpgUJbHlJmxuBsuioPymCAOaQUesRI3Yi053ZfABB+a7wU3tf9h0uNCUoWYr7w7e/N/3wDaphltH8A9MzDc9fFHjcen3+T4Ehl+0MKpY44eWTV6K22vQHuhO4h1c8ruvNTBlimTK27fc3uHhm9TL6ieXqf3UrSqA9bNqfnwHfFVdKXOMZ0cPPit7r6f3PmiVXteE+WijkN8PPzZtfEqU58jPKh3tAo4QzXYEgyGztY9NSGqCfvqBXMYIJgKgUPO3f5aUDmLwI2f1gZvWBsJ+VYHtMonBrIDg5U1bKsSzsQXNZZ+k55Zxe/1i8TEI4YsFGTYGco1UOd1KE+67XaQoPqAPyorNYhWeVmKSiGiHLhFt2RaE1mUf64pKTcyINyXmVlJKIMLIN4yvgAYFREAu/OA1GY6lt8=";

    public Tablist(PlayerWrapper receiver){
        this.receiver = receiver;
        defaultTablist();
        update();
    }

    public void startUpdate(){
        updates = new HashMap<>();
    }

    public void endUpdate(){
        if (currentPlayers != null){
            ArrayList<ServerPlayer> playersToRemove = new ArrayList<>();
            for (String key : updates.keySet()){
                playersToRemove.add(currentPlayers.get(key));
            }

            ClientboundPlayerInfoPacket packet = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, playersToRemove);
            ((CraftPlayer) receiver.getSource()).getHandle().connection.send(packet);
        } else {
            currentPlayers = new HashMap<>();
        }

        ClientboundPlayerInfoPacket packet = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, updates.values());
        ((CraftPlayer) receiver.getSource()).getHandle().connection.send(packet);

        for (String key : updates.keySet()){
            currentPlayers.put(key, updates.get(key));
        }

        // null the updates variable in case this will be used for future updates
        updates = null;
    }

    public void update(){
        startUpdate();

        // left-most column should list players

        updateSlot(0, StringUtils.center("Players", lineWidth, "-"));
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        for (int i = 0; i < Math.min(19, players.length); i++){
            var nmsPlayer = ((CraftPlayer) players[i]).getHandle();
            Property textureProperty = getTextureProperty(nmsPlayer);
            updateSlot(i+1, players[i].getPlayerListName(), textureProperty.getValue(), textureProperty.getSignature());
        }

        // right-most column should list statistics

        updateSlot(60, StringUtils.center("Stats", lineWidth, "-"));
        updateSlot(61, "Health: " + receiver.getEntityHealth() + "<3");

        endUpdate();
    }

    public void defaultTablist(){
        removeAllPlayers();
        startUpdate();
        fillBoard();
        endUpdate();
    }

    public void updateSlot(int index, String listName){
        String id = " " + index/20 + index%20/2 + index%20%2;
        ServerPlayer oldPlayer = currentPlayers.get(id);
        Property textureProperty = getTextureProperty(oldPlayer);
        updateSlot(index, listName, textureProperty.getValue(), textureProperty.getSignature());
    }

    public void updateSlot(int index, String listName, String texture, String signature){
        String id = " " + index/20 + index%20/2 + index%20%2;
        updates.put(id,
                createPlayer(
                        id,
                        listName,
                        texture,
                        signature
                )
        );
    }

    private Property getTextureProperty(ServerPlayer player){
        Optional<Property> textureOptional = player.getGameProfile().getProperties().get("textures").stream().findFirst();
        if (textureOptional.isPresent()){
            Property textureProperty = textureOptional.get();
            return new Property("texture", textureProperty.getValue(), textureProperty.getSignature());
        } else {
            return new Property("texture", blankTextureValue, blankTextureSignature);
        }
    }

    private void removeAllPlayers() {
        Collection<? extends Player> playersBukkit = Bukkit.getOnlinePlayers();
        ServerPlayer[] playersNMS = new ServerPlayer[playersBukkit.size()];
        int current = 0;
        for (Player player : playersBukkit) {
            playersNMS[current] = ((CraftPlayer) player).getHandle();
            current++;
        }
        ClientboundPlayerInfoPacket packet = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, playersNMS);
        ((CraftPlayer) receiver.getSource()).getHandle().connection.send(packet);
    }

    private ServerPlayer createPlayer(String name, String listName, String texture, String signature) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel worldserver = server.getLevel(ServerLevel.OVERWORLD);
        //PlayerInteractManager playerinteractmanager = new PlayerInteractManager(worldserver);
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", new Property("textures", texture, signature));
        ServerPlayer player = new ServerPlayer(server, worldserver, profile);
        player.listName = new TextComponent(StringUtils.rightPad(" " + listName, lineWidth));
        return player;
    }

    private void addPlayers(Player receiver, ServerPlayer... createdPlayers) {
        ClientboundPlayerInfoPacket packet = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, createdPlayers);
        ((CraftPlayer) receiver).getHandle().connection.send(packet);
    }

    private void fillBoard(){
        for (int i = 0; i < boardSize; i++){
            String id = " " + i/20 + i%20/2 + i%20%2;
            updates.put(id,
                createPlayer(
                    id,
                    "",
                    blankTextureValue,
                    blankTextureSignature
                )
            );
        }
    }

}
