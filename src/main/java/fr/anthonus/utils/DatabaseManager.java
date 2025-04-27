package fr.anthonus.utils;

import fr.anthonus.LOGs;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/UserData/UserData.db";

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS Users (" +
                    "user_id INTEGER PRIMARY KEY," +
                    "xp INTEGER NOT NULL," +
                    "level INTEGER NOT NULL" +
                    ");";

            conn.createStatement().execute(createTableQuery);
            LOGs.sendLog("Base de données initialisée avec succès", "LOADING");

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement de la base de données : " + e);
        }
    }

    public static void saveUser(User user){
        String query = "INSERT INTO Users (user_id, xp, level) VALUES (?, ?, ?) " +
                "ON CONFLICT(user_id) DO UPDATE SET xp = ?, level = ?;";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, user.getUserId());
            stmt.setInt(2, user.getXp());
            stmt.setInt(3, user.getLevel());

            stmt.setInt(4, user.getXp());
            stmt.setInt(5, user.getLevel());
            stmt.executeUpdate();

            LOGs.sendLog("Utilisateur sauvegardé avec succès : " + user.getUserId(), "LOADING");

        } catch (SQLException e) {
            LOGs.sendLog("Erreur lors de la sauvegarde de l'utilisateur : " + e, "ERROR");
        }
    }

    public static User loadUser(long userId) {
        String query = "SELECT xp, level FROM Users WHERE user_id = ?;";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, userId);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                int xp = result.getInt("xp");
                int level = result.getInt("level");

                return new User(userId, xp, level);
            }

        } catch (SQLException e) {
            LOGs.sendLog("Erreur lors du chargement de l'utilisateur : " + e, "ERROR");
        }

        return null;
    }
}
