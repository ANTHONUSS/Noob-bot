package fr.anthonus.listeners;

import fr.anthonus.utils.*;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.LevelManager;
import fr.anthonus.utils.managers.SettingsManager;
import fr.anthonus.utils.managers.UserManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        long userId = event.getAuthor().getIdLong();
        User user = UserManager.users.get(userId);

        if (user != null && user.getLastMessageTime() != null &&
            Instant.now().minusSeconds(SettingsManager.timeBeforeXP).isBefore(user.getLastMessageTime())) {
            return;
        }

        if (user.getXp() >= LevelManager.maxXp) {
            return;
        }

        int levelBefore = LevelManager.getLevelFromXP(user.getXp());

        user.addXp(10);
        DatabaseManager.updateXp(userId, user.getXp());

        int levelAfter = LevelManager.getLevelFromXP(user.getXp());
        if (levelBefore != levelAfter) {
            user.setLevel(levelAfter);
            DatabaseManager.updateLevel(userId, levelAfter);
            LevelManager.sendLevelUpMessage(userId, levelAfter);
        }

        user.setLastMessageTime(Instant.now());

        LevelManager.checkAndUpdateUserRole(userId, levelAfter);
    }
}
