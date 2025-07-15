package fr.anthonus.commands.users;

import fr.anthonus.logs.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.LevelManager;
import fr.anthonus.utils.managers.CodeUserManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import fr.anthonus.logs.logTypes.*;

import static fr.anthonus.Main.*;

public class StatsCommand extends Command {
    long targetUserId;

    public StatsCommand(SlashCommandInteractionEvent event, long userId) {
        super(event);
        this.targetUserId = userId;

        LOGs.sendLog("Commande /stats initialisée", DefaultLogType.COMMAND);
    }

    @Override
    public void run() {
        if (targetUserId == -1) targetUserId = currentEvent.getUser().getIdLong();

        CodeUser codeUser = CodeUserManager.users.get(targetUserId);

        if (codeUser == null) {
            currentEvent.reply("## :x: L'utilisateur n'existe pas.").setEphemeral(true).queue();
            LOGs.sendLog("L'utilisateur n'existe pas : " + targetUserId, DefaultLogType.ERROR);
            return;
        }

        String name;
        try {
            name = jda.retrieveUserById(targetUserId).complete().getName();
        } catch (Exception e) {
            currentEvent.reply("## :x: Impossible de récupérer les informations de l'utilisateur.").setEphemeral(true).queue();
            LOGs.sendLog("Erreur lors de la récupération de l'utilisateur : " + targetUserId, DefaultLogType.ERROR);
            return;
        }

        String time;
        int minutes = codeUser.getNbVoiceTimeSpent();
        if (minutes < 60) {
            if (minutes == 0) {
                time = "Aucun temps passé en voc";
            } else if (minutes == 1) {
                time = "1 minute";
            } else {
                time = minutes + " minute(s)";
            }
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            String hourString = (hours == 1) ? " heure " : " heures ";
            String minuteString = (remainingMinutes == 1) ? " minute" : " minutes";
            time = hours + hourString + (remainingMinutes > 0 ? remainingMinutes + minuteString : "");
        }

        StringBuilder fieldContentBuilder = new StringBuilder();
        if (codeUser.getXp() < LevelManager.maxXp) {
            fieldContentBuilder.append("**:face_with_monocle: XP pour le prochain niveau : **")
                    .append(LevelManager.getXPForLevelUp(codeUser.getXp())).append("\n");
            fieldContentBuilder.append("**:up: Nombre de niveaux pour le prochain palier : **")
                    .append(LevelManager.getLevelsForNextPalier(codeUser.getLevel())).append("\n");
        }
        fieldContentBuilder.append("**:speech_balloon: Nombre de messages envoyés : **")
                .append(codeUser.getNbMessagesSent()).append(" messages\n");
        fieldContentBuilder.append("**:loud_sound: Temps passé en voc : **").append(time);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Statistiques de " + name);
        embed.addField("", "**:chart_with_upwards_trend: XP : **" + codeUser.getXp(), true);
        embed.addField("", "**:trophy: Niveau : **" + codeUser.getLevel(), true);
        embed.addField("", "**:medal: Palier actuel : **<@&" + LevelManager.getPalier(codeUser.getLevel()).getIdLong() + ">", true);

        embed.addField("", fieldContentBuilder.toString(), false);

        currentEvent.replyEmbeds(embed.build()).queue();
    }

}
