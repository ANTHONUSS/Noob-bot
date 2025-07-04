package fr.anthonus.utils.managers;

import fr.anthonus.Main;
import fr.anthonus.logs.LOGs;
import fr.anthonus.logs.logTypes.DefaultLogType;
import fr.anthonus.utils.CodeUser;

import java.util.List;

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
        logMessage.append(pingAdmins);

        guild.getTextChannelById(SettingsManager.logsChannel).sendMessage(logMessage.toString()).queue();

    }

    public static void sendLinkLog(long userId, String messageContent, long channelId) {
        StringBuilder logMessage = new StringBuilder();

        logMessage.append("# L'utilisateur <@").append(userId).append("> a tenté d'envoyer un lien d'invitation Discord.\n");
        logMessage.append("## Salon : <#").append(channelId).append(">\n");
        logMessage.append("## Message : `").append(messageContent).append("`\n");
        logMessage.append(pingAdmins);

        guild.getTextChannelById(SettingsManager.logsChannel).sendMessage(logMessage.toString()).queue();

    }
}
