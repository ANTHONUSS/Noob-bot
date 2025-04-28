package fr.anthonus.commands.admin;

import fr.anthonus.commands.Command;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.LevelManager;
import fr.anthonus.utils.User;
import fr.anthonus.utils.managers.UserManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static fr.anthonus.Main.jda;

public class SetXpCommand extends Command {
    private final long targetUserId;
    private final int xp;

    public SetXpCommand(SlashCommandInteractionEvent event, long targetUserId, int xp) {
        super(event);
        this.targetUserId = targetUserId;
        this.xp = xp;
    }

    @Override
    public void run() {
        User user = UserManager.users.get(targetUserId);
        if (user == null) {
            currentEvent.reply("## :x: L'utilisateur n'existe pas.").setEphemeral(true).queue();
            return;
        }

        user.setXp(xp);
        DatabaseManager.updateXp(targetUserId, xp);

        int level = LevelManager.getLevelFromXP(xp);
        user.setLevel(level);
        DatabaseManager.updateLevel(targetUserId, level);

        currentEvent.reply("## :white_check_mark: XP de l'utilisateur " + jda.retrieveUserById(targetUserId).complete().getName() + " mis à jour à " + xp + " XP.").setEphemeral(true).queue();

        LevelManager.checkAndUpdateUserRole(targetUserId, level);

    }
}
