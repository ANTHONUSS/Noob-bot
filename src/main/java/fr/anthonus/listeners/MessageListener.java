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

        if (codeUser != null && codeUser.getLastMessageTime() != null &&
            Instant.now().minusSeconds(SettingsManager.timeBeforeXP).isBefore(codeUser.getLastMessageTime())) {
            return;
        }

        if (codeUser.getXp() >= LevelManager.maxXp) {
            return;
        }

        int levelBefore = LevelManager.getLevelFromXP(codeUser.getXp());

        codeUser.addXp(LevelManager.xp_per_msg);
        DatabaseManager.updateXp(userId, codeUser.getXp());

        int levelAfter = LevelManager.getLevelFromXP(codeUser.getXp());
        if (levelBefore != levelAfter) {
            codeUser.setLevel(levelAfter);
            DatabaseManager.updateLevel(userId, levelAfter);
            LevelManager.sendLevelUpMessage(userId, levelAfter);
            LevelManager.checkAndUpdateUserRole(userId, levelAfter);
        }

        codeUser.setLastMessageTime(Instant.now());

    }
}
