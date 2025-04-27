package fr.anthonus.utils;

import fr.anthonus.LOGs;
import fr.anthonus.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.Map;

import static fr.anthonus.Main.*;

public class UserManager {
    public static final Map<Long, User> users = new HashMap<>();

    public static void loadUsers() {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new RuntimeException("Erreur lors du chargement des utilisateurs : le serveur n'a pas été trouvé");
        }

        LOGs.sendLog("Chargement des utilisateurs du serveur...", "FILE_LOADING");

        guild.loadMembers().onSuccess(members -> {
            for (Member member : members) {
                if (member.getUser().isBot())
                    continue; // Ignore les bots

                long userId = member.getIdLong();
                User user = DatabaseManager.loadUser(userId);

                if (user == null) {
                    user = new User(userId, 0, 0);
                    DatabaseManager.saveUser(user);
                    users.put(userId, user);
                    LOGs.sendLog("Nouvel utilisateur ajouté et chargé avec l'ID " + userId, "FILE_LOADING");
                } else {
                    users.put(userId, user);
                    LOGs.sendLog("Utilisateur chargé avec l'ID " + userId, "FILE_LOADING");
                }
            }
            LOGs.sendLog("Tous les utilisateurs ont été chargés", "LOADING");

        }).onError(error -> {
            LOGs.sendLog("Erreur lors du chargement des utilisateurs : " + error.getMessage(), "ERROR");

        });
    }
}
