package fr.anthonus.utils.managers;

import fr.anthonus.LOGs;
import fr.anthonus.utils.User;

import java.sql.*;

import static fr.anthonus.Main.jda;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/UserData/UserData.db";

    /**
     * Initialise la base de données SQLite et crée la table Users si elle n'existe pas déjà.
     */
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

    /**
     * Sauvegarde un utilisateur dans la base de données (ou le met à jour si jamais iul existe déjà).
     * @param user L'utilisateur à sauvegarder.
     */
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

            LOGs.sendLog("Utilisateur sauvegardé avec succès : " + jda.retrieveUserById(user.getUserId()).complete().getName(), "FILE_LOADING");

        } catch (SQLException e) {
            LOGs.sendLog("Erreur lors de la sauvegarde de l'utilisateur : " + e, "ERROR");
        }
    }

    /**
     * Charge un utilisateur depuis la base de données.
     * @param userId L'ID de l'utilisateur à charger.
     * @return L'utilisateur chargé, ou null s'il n'existe pas.
     */
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

    /**
     * Met à jour l'XP d'un utilisateur dans la base de données.
     * @param userId L'ID de l'utilisateur.
     * @param newXp La nouvelle valeur d'XP.
     */
    public static void updateXp(long userId, int newXp) {
        String query = "UPDATE Users SET xp = ? WHERE user_id = ?;";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, newXp);
            stmt.setLong(2, userId);
            stmt.executeUpdate();

            // En commentaire parce que ça spam le log
//            LOGs.sendLog("XP mis à jour avec succès pour l'utilisateur : " + jda.retrieveUserById(userId).complete().getName(), "FILE_LOADING");


        } catch (SQLException e) {
            LOGs.sendLog("Erreur lors de la mise à jour de l'XP pour l'utilisateur : " + e, "ERROR");
        }
    }

    /**
     * Met à jour le niveau d'un utilisateur dans la base de données.
     * @param userId L'ID de l'utilisateur.
     * @param newLevel La nouvelle valeur de niveau.
     */
    public static void updateLevel(long userId, int newLevel) {
        String query = "UPDATE Users SET level = ? WHERE user_id = ?;";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, newLevel);
            stmt.setLong(2, userId);
            stmt.executeUpdate();

            LOGs.sendLog("Niveau mis à jour avec succès pour l'utilisateur : " + jda.retrieveUserById(userId).complete().getName(), "FILE_LOADING");

        } catch (SQLException e) {
            LOGs.sendLog("Erreur lors de la mise à jour du niveau pour l'utilisateur : " + e, "ERROR");
        }
    }
}
