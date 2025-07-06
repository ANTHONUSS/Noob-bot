package fr.anthonus.utils.managers;

import fr.anthonus.utils.CodeUser;

import static fr.anthonus.Main.guild;

public class AdminManager {
    private static final String pingAdmins = "<@&1363254838288318794> | <@&1363254575426965556> | <@&1363255271836614909>";

    public static void sendMuteLog(CodeUser codeUser, long channelId) {
        StringBuilder logMessage = new StringBuilder();

        logMessage.append("# L'utilisateur <@").append(codeUser.getUserId()).append("> a été muté pour spam.\n");
        logMessage.append("## Salon : <#").append(channelId).append(">\n");
        logMessage.append("## Messages :\n");
        for (String message : codeUser.getMessagesString()) {
            logMessage.append("- `").append(message).append("`\n");
        }
        logMessage.append("## Mute pendant : `");
        if (codeUser.getScore() <= -5){
            logMessage.append("1 jour");
        } else if (codeUser.getScore() <= -2) {
            logMessage.append("12 heures");
        } else if (codeUser.getScore() <= 1) {
            logMessage.append("45 minutes");
        } else {
            logMessage.append("10 minutes");
        }
        logMessage.append("`\n");
        logMessage.append(pingAdmins);

        guild.getTextChannelById(SettingsManager.logsChannel).sendMessage(logMessage.toString()).queue();

    }

    public static void sendLinkLog(long userId, String messageContent, long channelId, boolean isMuted) {
        StringBuilder logMessage = new StringBuilder();

        logMessage.append("# L'utilisateur <@").append(userId).append("> a tenté d'envoyer un lien d'invitation Discord.\n");
        logMessage.append("## Salon : <#").append(channelId).append(">\n");
        logMessage.append("## Message : `").append(messageContent).append("`\n");
        if (isMuted) {
            logMessage.append("## L'utilisateur a été mute.\n");
        } else {
            logMessage.append("## L'utilisateur n'a pas été mute.\n");
        }
        logMessage.append(pingAdmins);

        guild.getTextChannelById(SettingsManager.logsChannel).sendMessage(logMessage.toString()).queue();

    }
}
