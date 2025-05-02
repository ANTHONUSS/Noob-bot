package fr.anthonus.utils.managers;

import fr.anthonus.logs.LOGs;
import fr.anthonus.utils.CodeUser;
import net.dv8tion.jda.api.entities.Member;
import fr.anthonus.logs.logTypes.*;

import java.util.HashMap;
import java.util.Map;

import static fr.anthonus.Main.*;

public class CodeUserManager {
    public static final Map<Long, CodeUser> users = new HashMap<>();

    /**
     * Charge tous les utilisateurs du serveur en mémoire et les ajoute à la base de données s'ils n'y sont pas déjà.
     */
    public static void loadUsers() {
        if (guild == null) {
            throw new RuntimeException("Erreur lors du chargement des utilisateurs : le serveur n'a pas été trouvé");
        }

        LOGs.sendLog("Chargement des utilisateurs du serveur...", CustomLogType.FILE_LOADING);

        guild.loadMembers().onSuccess(members -> {
            for (Member member : members) {
                if (member.getUser().isBot())
                    continue; // Ignore les bots

                long userId = member.getIdLong();
                CodeUser codeUser = DatabaseManager.loadUser(userId);

                if (codeUser == null) {
                    codeUser = new CodeUser(userId, 0, 0, 0, 0);
                    DatabaseManager.saveUser(codeUser);
                    users.put(userId, codeUser);
                    LOGs.sendLog("Nouvel utilisateur ajouté et chargé : " + jda.retrieveUserById(userId).complete().getName(), CustomLogType.FILE_LOADING);
                } else {
                    users.put(userId, codeUser);
                    LOGs.sendLog("Utilisateur chargé : " + jda.retrieveUserById(userId).complete().getName(), CustomLogType.FILE_LOADING);
                }
            }
            LOGs.sendLog("Tous les utilisateurs ont été chargés", CustomLogType.LOADING);

        }).onError(error -> {
            LOGs.sendLog("Erreur lors du chargement des utilisateurs : " + error.getMessage(), DefaultLogType.ERROR);

        });
    }

}
