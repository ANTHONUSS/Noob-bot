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
        sortedUsers = sortedUsers.subList(0, Math.min(sortedUsers.size(), 5));

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(":trophy: Leaderboard :trophy:");
        int i = 1;
        for (CodeUser user : sortedUsers) {
            String userName = jda.retrieveUserById(user.getUserId()).complete().getName();

            String rankEmoji;
            switch (i) {
                case 1 -> rankEmoji = ":first_place:";
                case 2 -> rankEmoji = ":second_place:";
                case 3 -> rankEmoji = ":third_place:";
                case 4 -> rankEmoji = ":four:";
                case 5 -> rankEmoji = ":five:";
                default -> rankEmoji = ":medal:";
            }

            embed.addField("**" + rankEmoji + userName + "**",
                    ":arrow_right: **Niveau **" + LevelManager.getLevelFromXP(user.getXp()) + " | (" + user.getXp() + " XP)",
                    false
            );

            i++;
        }

        currentEvent.replyEmbeds(embed.build()).queue();
    }
}
