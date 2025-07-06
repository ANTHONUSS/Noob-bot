package fr.anthonus.commands.admin;

import fr.anthonus.commands.Command;
import fr.anthonus.logs.LOGs;
import fr.anthonus.logs.logTypes.DefaultLogType;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.CodeUserManager;
import fr.anthonus.utils.managers.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SetUserScoreCommand extends Command {
    private final long targetUserId;
    private final int score;

    public SetUserScoreCommand(SlashCommandInteractionEvent event, long targetUserId, int score) {
        super(event);
        this.targetUserId = targetUserId;
        this.score = score;

        LOGs.sendLog("Commande /set-user-score initialisée", DefaultLogType.COMMAND);
    }

    @Override
    public void run() {
        CodeUser targetUser = CodeUserManager.users.get(targetUserId);
        if (targetUser == null) {
            currentEvent.reply("L'utilisateur avec l'ID " + targetUserId + " n'existe pas.").setEphemeral(true).queue();
            LOGs.sendLog("L'utilisateur avec l'ID " + targetUserId + " n'existe pas.", DefaultLogType.ERROR);
            return;
        }

        targetUser.setScore(score);
        DatabaseManager.updateScore(targetUserId, score);
        currentEvent.reply("Le score de l'utilisateur <@" + targetUserId + "> a été mis à jour à " + score + ".").setEphemeral(true).queue();
        LOGs.sendLog("Le score de l'utilisateur " + targetUserId + " a été mis à jour à " + score + ".", DefaultLogType.COMMAND);
    }
}
