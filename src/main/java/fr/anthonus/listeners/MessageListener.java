package fr.anthonus.listeners;

import fr.anthonus.logs.LOGs;
import fr.anthonus.logs.logTypes.DefaultLogType;
import fr.anthonus.utils.*;
import fr.anthonus.utils.managers.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MessageListener extends ListenerAdapter {
    private static final int MAX_MESSAGES_IN_A_ROW = 8; // Nombre maximum de messages autorisés en une seule fois

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        boolean isAllowed = verifyMessage(event);

        if (isAllowed) isAllowed = verifySpam(event);

        if (isAllowed) updateUserStats(event);

    }

    private boolean verifyMessage(MessageReceivedEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) return true; // Les administrateurs sont exemptés de la vérification.

        String content = event.getMessage().getContentRaw();
        // Vérification des liens d'invitation Discord
        if (content.matches("(?:https?://)?(?:www\\.)?(?:discord\\.gg|discord(app)?\\.com/invite)/[a-zA-Z0-9\\-]+")) {

            event.getMessage().delete().complete();

            event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Les liens d'invitation Discord ne sont pas autorisés.").queue();
            LOGs.sendLog(event.getAuthor().getName() + " a tenté d'envoyer un lien d'invitation Discord.", DefaultLogType.ADMIN);
            AdminManager.sendLinkLog(event.getAuthor().getIdLong(), content, event.getChannel().getIdLong());

            return false;

        }

        return true;
    }

    private boolean verifySpam(MessageReceivedEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) return true; // Les administrateurs sont exemptés de la vérification.

        if (event.getMember().isTimedOut()) {
            event.getMessage().delete().complete(); // delete au cas où il envoie un message juste avant son timeout
            return false;
        }

        long userId = event.getAuthor().getIdLong();
        CodeUser codeUser = CodeUserManager.users.get(userId);
        if (Instant.now().minusSeconds(3).isBefore(codeUser.getLastMessageTime())) {
            codeUser.addMessageSentInARow(event.getMessage().getIdLong());
            codeUser.addMessageString(event.getMessage().getContentRaw());

            codeUser.setLastMessageTime(Instant.now());

        } else {
            codeUser.resetMessagesSentInARow();
            codeUser.resetMessagesString();
            codeUser.addMessageSentInARow(event.getMessage().getIdLong());
            codeUser.addMessageString(event.getMessage().getContentRaw());

            codeUser.setLastMessageTime(Instant.now());

        }

        if (codeUser.getNbMessageSentInARow() > MAX_MESSAGES_IN_A_ROW) {
            for (long messageId : codeUser.getMessagesId()) {
                event.getChannel().retrieveMessageById(messageId).queue(
                        message -> {
                            message.delete().queue();
                        },
                        throwable -> LOGs.sendLog("Erreur lors de la suppression du message ID " + messageId + ": " + throwable.getMessage(), DefaultLogType.ERROR)
                );
            }
            // Mute l'utilisateur pour 15 minutes
            event.getGuild().timeoutFor(event.getMember(), Duration.ofMinutes(15)).queue();

            event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Vous avez envoyé trop de messages en une seule fois. Vous avez été mute pendant 15 minutes.").queue();
            LOGs.sendLog(event.getAuthor().getName() + " a envoyé trop de messages en une seule fois. Il a été mute 15 minutes.", DefaultLogType.ADMIN);
            AdminManager.sendMuteLog(codeUser, event.getChannel().getIdLong());

            codeUser.resetMessagesSentInARow();
            codeUser.resetMessagesString();

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
