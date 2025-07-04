package fr.anthonus.listeners;

import fr.anthonus.logs.LOGs;
import fr.anthonus.logs.logTypes.DefaultLogType;
import fr.anthonus.utils.*;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.LevelManager;
import fr.anthonus.utils.managers.SettingsManager;
import fr.anthonus.utils.managers.CodeUserManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        boolean isAllowed = verifyMessage(event);

        if (isAllowed) updateUserStats(event);

    }

    private boolean verifyMessage(MessageReceivedEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) return true; // Les administrateurs sont exemptés de la vérification

        String content = event.getMessage().getContentRaw();
        // Vérification des liens d'invitation Discord
        if (content.matches("(?:https?:\\/\\/)?(?:www\\.)?(?:discord\\.gg|discord(app)?\\.com\\/invite)\\/[a-zA-Z0-9\\-]+")) {
            event.getMessage().delete().complete();
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Les liens d'invitation Discord ne sont pas autorisés.").queue();
            LOGs.sendLog(event.getAuthor().getName() + " a tenté d'envoyer un lien d'invitation Discord.", DefaultLogType.ADMIN);
            return false;

        }

        return true;
    }

    private void updateUserStats(MessageReceivedEvent event) {
        long userId = event.getAuthor().getIdLong();
        CodeUser codeUser = CodeUserManager.users.get(userId);

        // Ajout d'un message envoyé pour l'utilisateur
        codeUser.addNbMessageSent(1);
        DatabaseManager.updateNbMessagesSent(userId, codeUser.getNbMessagesSent());

        // verification dernier message envoyé
        if (codeUser.getLastMessageTime() != null && Instant.now().minusSeconds(SettingsManager.timeBeforeXP).isBefore(codeUser.getLastMessageTime())) {
            return;
        }

        // ajout de l'xp
        LevelManager.addXpAndVerify(codeUser, LevelManager.xp_per_msg);

        codeUser.setLastMessageTime(Instant.now());
    }
}
