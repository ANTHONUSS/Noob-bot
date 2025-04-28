package fr.anthonus.commands.users;

import fr.anthonus.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.LevelManager;
import fr.anthonus.utils.managers.CodeUserManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static fr.anthonus.Main.*;

public class StatsCommand extends Command {
    long targetUserId;

    public StatsCommand(SlashCommandInteractionEvent event, long userId) {
        super(event);
        this.targetUserId = userId;

        LOGs.sendLog("Commande /stats initialisée", "COMMAND");
    }

    @Override
    public void run() {
        if (targetUserId == -1) targetUserId = currentEvent.getUser().getIdLong();

        CodeUser codeUser = CodeUserManager.users.get(targetUserId);

        if (codeUser == null) {
            currentEvent.reply("## :x: L'utilisateur n'existe pas.").setEphemeral(true).queue();
            LOGs.sendLog("L'utilisateur n'existe pas : " + targetUserId, "ERROR");
            return;
        }

        String name;
        try {
            name = jda.retrieveUserById(targetUserId).complete().getName();
        } catch (Exception e) {
            currentEvent.reply("## :x: Impossible de récupérer les informations de l'utilisateur.").setEphemeral(true).queue();
            LOGs.sendLog("Erreur lors de la récupération de l'utilisateur : " + targetUserId, "ERROR");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Statistiques de " + name);
        embed.addField("XP", String.valueOf(codeUser.getXp()), true);
        embed.addField("Niveau", String.valueOf(codeUser.getLevel()), true);

        if (codeUser.getXp() < LevelManager.maxXp) {
            embed.addField("XP pour le prochain niveau", String.valueOf(LevelManager.getXPForLevelUp(codeUser.getXp())), false);
        }

        embed.addField("Palier actuel", LevelManager.getPalier(codeUser.getLevel()).getName(), false);

        currentEvent.replyEmbeds(embed.build()).queue();
    }

}
