package fr.anthonus.listeners;

import fr.anthonus.utils.*;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.LevelManager;
import fr.anthonus.utils.managers.SettingsManager;
import fr.anthonus.utils.managers.CodeUserManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        long userId = event.getAuthor().getIdLong();
        CodeUser codeUser = CodeUserManager.users.get(userId);

        // Ajout d'un message envoyé pour l'utilisateur
        codeUser.addNbMessageSent(1);
        DatabaseManager.updateNbMessagesSent(userId, codeUser.getNbMessagesSent());

        // verification dernier message envoyé
        if (codeUser != null && codeUser.getLastMessageTime() != null &&
            Instant.now().minusSeconds(SettingsManager.timeBeforeXP).isBefore(codeUser.getLastMessageTime())) {
            return;
        }

        // ajout de l'xp
        LevelManager.addXpAndVerify(codeUser, LevelManager.xp_per_msg);

        codeUser.setLastMessageTime(Instant.now());

    }
}
