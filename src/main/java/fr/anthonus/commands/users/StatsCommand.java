package fr.anthonus.commands.users;

import fr.anthonus.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.LevelManager;
import fr.anthonus.utils.User;
import fr.anthonus.utils.UserManager;
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

        User user = UserManager.users.get(targetUserId);

        if (user == null) {
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
        embed.addField("XP", String.valueOf(user.getXp()), true);
        embed.addField("Niveau", String.valueOf(user.getLevel()), true);

        if (user.getXp() < LevelManager.maxXp) {
            embed.addField("XP pour le prochain niveau", String.valueOf(LevelManager.getXPForLevelUp(user.getXp())), false);
        }

        embed.addField("Palier actuel", LevelManager.getPalier(user.getLevel()).getAsMention(), false);

        currentEvent.replyEmbeds(embed.build()).queue();
    }

}
