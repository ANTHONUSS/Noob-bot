package fr.anthonus.commands.users;

import fr.anthonus.logs.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.CodeUserManager;
import fr.anthonus.utils.managers.LevelManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import fr.anthonus.logs.logTypes.*;

import java.util.ArrayList;
import java.util.List;

import static fr.anthonus.Main.jda;

public class LeaderBoardCommand extends Command {
    public LeaderBoardCommand(SlashCommandInteractionEvent event) {
        super(event);

        LOGs.sendLog("Commande /leaderboard initialisée", DefaultLogType.COMMAND);
    }

    @Override
    public void run() {
        List<CodeUser> sortedUsers = new ArrayList<>(CodeUserManager.users.values());
        sortedUsers.sort((user1, user2) -> Integer.compare(user2.getXp(), user1.getXp()));
        sortedUsers = sortedUsers.subList(0, Math.min(sortedUsers.size(), 10));

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(":trophy: Leaderboard :trophy:");
        int i = 1;
        for (CodeUser user : sortedUsers) {
            String userName = jda.retrieveUserById(user.getUserId()).complete().getName();

            String time;
            int minutes = user.getNbVoiceTimeSpent();
            if (minutes < 60) {
                time = minutes + " minute(s)";
            } else {
                int hours = minutes / 60;
                int remainingMinutes = minutes % 60;
                time = hours + " heure(s) " + (remainingMinutes > 0 ? remainingMinutes + " minute(s)" : "");
            }

            embed.addField(i++ +". " + userName,
                    "**XP :** " + user.getXp() +
                    "\n**Niveau :** " + user.getLevel() +
                    "\n**Palier :** " + LevelManager.getPalier(user.getLevel()).getName() +
                    "\n**Nombre de messages envoyés :** " + user.getNbMessagesSent() +
                    "\n**Temps passé en voc de-mute :** " + time,
                    false);
            if (i == sortedUsers.size())
                embed.addBlankField(false);
        }

        currentEvent.replyEmbeds(embed.build()).queue();
    }
}
