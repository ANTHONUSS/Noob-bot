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

public class MessageListener extends ListenerAdapter {
    private static final int MAX_MESSAGES_IN_A_ROW = 8; // Nombre maximum de messages autorisés en une seule fois

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        boolean isAllowed = verifySpam(event);

        if (isAllowed) isAllowed = verifyMessage(event);

        if (isAllowed) updateUserStats(event);



    }

    private boolean verifySpam(MessageReceivedEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) return true; // Les administrateurs sont exemptés de la vérification.

        if (event.getMember().isTimedOut()) {
            event.getMessage().delete().complete(); // delete au cas où il envoie un message juste avant son timeout
            return false;
        }

        long userId = event.getAuthor().getIdLong();
        CodeUser codeUser = CodeUserManager.users.get(userId);
        if (Instant.now().minusSeconds(3).isBefore(codeUser.getLastSpamMessageTime())) {
            codeUser.addMessageSentInARow(event.getMessage().getIdLong());
            codeUser.addMessageString(event.getMessage().getContentRaw());

            codeUser.setLastSpamMessageTime(Instant.now());

        } else {
            codeUser.resetMessagesSentInARow();
            codeUser.resetMessagesString();
            codeUser.addMessageSentInARow(event.getMessage().getIdLong());
            codeUser.addMessageString(event.getMessage().getContentRaw());

            codeUser.setLastSpamMessageTime(Instant.now());

        }

        if (codeUser.getNbMessageSentInARow() > MAX_MESSAGES_IN_A_ROW) {
            codeUser.subtractScore(2);
            DatabaseManager.updateScore(userId, codeUser.getScore());
            for (long messageId : codeUser.getMessagesId()) {
                event.getChannel().retrieveMessageById(messageId).queue(
                        message -> {
                            message.delete().queue();
                        },
                        throwable -> LOGs.sendLog("Erreur lors de la suppression du message ID " + messageId + ": " + throwable.getMessage(), DefaultLogType.ERROR)
                );
            }
            // Mute l'utilisateur pour X minutes
            if (codeUser.getScore() <= -5){
                event.getGuild().timeoutFor(event.getMember(), Duration.ofDays(1)).queue();
            } else if (codeUser.getScore() <= -2) {
                event.getGuild().timeoutFor(event.getMember(), Duration.ofHours(12)).queue();
            } else if (codeUser.getScore() <= 1) {
                event.getGuild().timeoutFor(event.getMember(), Duration.ofMinutes(30)).queue();
            } else {
                event.getGuild().timeoutFor(event.getMember(), Duration.ofMinutes(10)).queue();
            }

            event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Vous avez envoyé trop de messages en une seule fois. Vous avez été mute.").queue();
            LOGs.sendLog(event.getAuthor().getName() + " a envoyé trop de messages en une seule fois. Il a été mute.", DefaultLogType.ADMIN);
            AdminManager.sendMuteLog(codeUser, event.getChannel().getIdLong());

            codeUser.resetMessagesSentInARow();
            codeUser.resetMessagesString();

            return false;
        }

        return true;
    }

    private boolean verifyMessage(MessageReceivedEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) return true; // Les administrateurs sont exemptés de la vérification.

        String content = event.getMessage().getContentRaw();
        // Vérification des liens d'invitation Discord
        if (content.matches("(?:https?://)?(?:www\\.)?(?:discord\\.gg|discord(app)?\\.com/invite)/[a-zA-Z0-9\\-]+")) {

            event.getMessage().delete().complete();

            event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Les liens d'invitation Discord ne sont pas autorisés.").queue();
            LOGs.sendLog(event.getAuthor().getName() + " a tenté d'envoyer un lien d'invitation Discord.", DefaultLogType.ADMIN);

            CodeUser codeUser = CodeUserManager.users.get(event.getAuthor().getIdLong());
            codeUser.subtractScore(1);
            DatabaseManager.updateScore(event.getAuthor().getIdLong(), codeUser.getScore());
            boolean isMuted = codeUser.getScore() < 0;
            if (isMuted) {
                event.getGuild().timeoutFor(event.getMember(), Duration.ofMinutes(15)).queue();
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Vous avez été mute pour avoir tenté d'envoyer un lien d'invitation Discord.").queue();
            }

            AdminManager.sendLinkLog(event.getAuthor().getIdLong(), content, event.getChannel().getIdLong(), isMuted);

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
