package fr.anthonus.commands.users;

import fr.anthonus.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.CodeUserManager;
import fr.anthonus.utils.managers.LevelManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

import static fr.anthonus.Main.jda;

public class LeaderBoardCommand extends Command {
    public LeaderBoardCommand(SlashCommandInteractionEvent event) {
        super(event);

        LOGs.sendLog("Commande /leaderboard initialisée", "COMMAND");
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
            embed.addField(i++ +". " + userName, "**XP :** " + user.getXp() + " | **Level :** " + user.getLevel() + " | **Palier :** " + LevelManager.getPalier(user.getLevel()).getName(), false);
        }

        currentEvent.replyEmbeds(embed.build()).queue();
    }
}
