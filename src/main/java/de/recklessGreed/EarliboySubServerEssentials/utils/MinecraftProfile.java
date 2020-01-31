package de.recklessGreed.EarliboySubServerEssentials.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.UUID;

public class MinecraftProfile {

    String username;
    UUID uuid;


    public MinecraftProfile(String username) {
        createFromUsername(username);
    }

    public MinecraftProfile(UUID uuid) {
        createFromUUID(uuid);
    }

    public void createFromUsername(String username) {
        this.username = username;
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            URLConnection connection = url.openConnection();
            Scanner jsonScanner = new Scanner(connection.getInputStream(), "UTF-8");
            String json = jsonScanner.next();

            JsonParser parser = new JsonParser();
            JsonObject tree = parser.parse(json).getAsJsonObject();

            String uuid = tree.get("id").getAsString();
            this.uuid = UUID.fromString(uuid);

            jsonScanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createFromUUID(UUID uuid) {
        this.uuid = uuid;
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""));
            URLConnection connection = url.openConnection();
            Scanner jsonScanner = new Scanner(connection.getInputStream(), "UTF-8");
            String json = jsonScanner.next();

            JsonParser parser = new JsonParser();
            JsonObject tree = parser.parse(json).getAsJsonObject();

            String username = tree.get("name").getAsString();
            this.username = username;

            jsonScanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }
}
