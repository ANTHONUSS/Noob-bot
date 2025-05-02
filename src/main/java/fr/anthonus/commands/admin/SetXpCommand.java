package fr.anthonus.commands.admin;

import fr.anthonus.logs.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.LevelManager;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.CodeUserManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import fr.anthonus.logs.logTypes.*;

import static fr.anthonus.Main.jda;

public class SetXpCommand extends Command {
    private final long targetUserId;
    private final int xp;

    public SetXpCommand(SlashCommandInteractionEvent event, long targetUserId, int xp) {
        super(event);
        this.targetUserId = targetUserId;
        this.xp = xp;

        LOGs.sendLog("Commande /set-xp initialisée", CustomLogType.COMMAND);
    }

    @Override
    public void run() {
        CodeUser codeUser = CodeUserManager.users.get(targetUserId);
        if (codeUser == null) {
            currentEvent.reply("## :x: L'utilisateur n'existe pas.").setEphemeral(true).queue();
            return;
        }

        codeUser.setXp(xp);
        DatabaseManager.updateXp(targetUserId, xp);

        int level = LevelManager.getLevelFromXP(xp);
        codeUser.setLevel(level);
        DatabaseManager.updateLevel(targetUserId, level);

        currentEvent.reply("## :white_check_mark: XP de l'utilisateur " + jda.retrieveUserById(targetUserId).complete().getName() + " mis à jour à " + xp + " XP.").setEphemeral(true).queue();

        LevelManager.checkAndUpdateUserRole(targetUserId, level);

    }
}
