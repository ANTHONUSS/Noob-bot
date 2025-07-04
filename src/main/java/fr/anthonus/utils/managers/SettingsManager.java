package fr.anthonus.utils.managers;

import java.io.FileReader;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.anthonus.logs.LOGs;
import fr.anthonus.logs.logTypes.*;

public class SettingsManager {
    public static long arrivalsChannel;
    public static long levelInfoChannel;
    public static long commandsChannel;
    public static long logsChannel;
    public static long timeBeforeXP;

    public static void loadSettings() {
        try (FileReader reader = new FileReader("data/settings.json")) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            arrivalsChannel = json.get("arrivalsChannel").getAsLong();
            LOGs.sendLog("Salon des arrivées chargé : " + arrivalsChannel, DefaultLogType.FILE_LOADING);
            levelInfoChannel = json.get("levelInfoChannel").getAsLong();
            LOGs.sendLog("Salon des annonces de niveaux chargé : " + levelInfoChannel, DefaultLogType.FILE_LOADING);
            commandsChannel = json.get("commandsChannel").getAsLong();
            LOGs.sendLog("Salon des commandes chargé : " + commandsChannel, DefaultLogType.FILE_LOADING);
            logsChannel = json.get("logsChannel").getAsLong();
            LOGs.sendLog("Salon des logs chargé : " + logsChannel, DefaultLogType.FILE_LOADING);
            timeBeforeXP = json.get("timeBeforeXP").getAsInt();
            LOGs.sendLog("Temps avant XP chargé : " + timeBeforeXP, DefaultLogType.FILE_LOADING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
