package fr.anthonus;

import fr.anthonus.exceptions.LOGs.LogTypeAlreadyExistsException;
import fr.anthonus.exceptions.LOGs.LogTypeDoesntExistsException;
import fr.anthonus.exceptions.LOGs.RVBFormatException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to send logs to the console and to a file.
 * Each logs can be colored and tagged with a custom type.
 *
 * @author ANTHONUS
 * @version 1.0
 */
public class LOGs {
    /**
     * This map contains the default and developer-created logs types with their RVB colors.
     */
    private static final Map<String, String> logTypes = new ConcurrentHashMap<>();

    static {
        logTypes.put("RESET", "\u001B[0m");
        logTypes.put("ERROR", "\u001B[31m");
        logTypes.put("DEFAULT", "\u001B[32m");

        try {
            fileExists();
        } catch (IOException e) {
            System.err.println(logTypes.get("ERROR") + "Error checking or creating logs.txt: " + e.getMessage() + logTypes.get("RESET"));
        }
    }

    /**
     * <p><b>Add a new log type to the map with it's RVB colors.</b></p>
     * <p>The type is not case-sensitive since it's always converted to uppercase.</p>
     *
     * @param type The type of the log to send (default ones are RESET, ERROR and DEFAULT).
     * @param r    The red value of the new color.
     * @param g    The green value of the new color.
     * @param b    The blue value of the new color.
     * @throws LogTypeAlreadyExistsException If the log type already exists.
     * @throws RVBFormatException            If the RVB values are not between 0 and 255.
     * @throws IllegalArgumentException      If the log type is null, empty or contains invalid characters.
     */
    public static void addLogType(String type, int r, int g, int b) throws LogTypeAlreadyExistsException, RVBFormatException {
        if (r > 255 || r < 0 || g > 255 || g < 0 || b > 255 || b < 0) {
            throw new RVBFormatException(r, g, b);
        }

        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("logTypes cannot be null or empty.");
        }
        if (!type.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("logType contains invalid characters: Only alphanumeric characters and underscores are allowed.");
        }

        if (logTypes.containsKey(type.toUpperCase())) {
            throw new LogTypeAlreadyExistsException(type);
        }


        String ansiColor = "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
        logTypes.put(type.toUpperCase(), ansiColor);
    }

    /**
     * <p><b>Send a log to the console and to a file with the colors.</b></p>
     *
     * @param message The message to send.
     * @param logType The type of the log to send.
     * @throws LogTypeDoesntExistsException If the log type doesn't exist.
     */
    public static void sendLog(String message, String logType) throws LogTypeDoesntExistsException {
        if (!logTypes.containsKey(logType.toUpperCase())) {
            throw new LogTypeDoesntExistsException(logType);
        }

        String timeMessage = "[" + java.time.LocalTime.now() + "] ";
        String color = logTypes.get(logType.toUpperCase());
        String enterMessage = logType.toUpperCase() + " ==> ";


        System.out.println(color + timeMessage + enterMessage + message + logTypes.get("RESET"));
        writeToFile(enterMessage + message);
    }

    /**
     * <p><b>Write the log in a file.</b></p>
     *
     * @param log The log to write in the file.
     */
    private static void writeToFile(String log) {
        File logFile = new File("logs.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(log + "\n");
            writer.newLine();
        } catch (IOException e) {
            System.err.println(logTypes.get("ERROR") + "impossible to write in logs.txt : " + e.getMessage() + logTypes.get("RESET"));
        }
    }

    private static void fileExists() throws IOException {
        File logFile = new File("logs.txt");
        if (!logFile.exists()) {
            if (logFile.createNewFile()) {
                System.out.println(logTypes.get("DEFAULT") + "logs.txt created." + logTypes.get("RESET"));
            } else {
                System.err.println(logTypes.get("ERROR") + "impossible to create logs.txt." + logTypes.get("RESET"));
            }
        }
    }
}
