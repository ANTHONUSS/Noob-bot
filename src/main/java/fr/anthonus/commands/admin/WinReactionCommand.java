package fr.anthonus.commands.admin;

import fr.anthonus.logs.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.CodeUserManager;
import fr.anthonus.utils.managers.LevelManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import fr.anthonus.logs.logTypes.*;

import java.util.ArrayList;
import java.util.List;

public class WinReactionCommand extends Command {
    private final long messageId;
    private final int xp;

    public WinReactionCommand(SlashCommandInteractionEvent event, long messageId, int xp) {
        super(event);
        this.messageId = messageId;
        this.xp = xp;

        LOGs.sendLog("Commande /win-reaction initialisée", DefaultLogType.COMMAND);
    }

    @Override
    public void run() {
        currentEvent.getChannel().retrieveMessageById(messageId).queue(message -> {
            MessagePoll poll = message.getPoll();
            if (poll == null) {
                currentEvent.reply("## :x: Le message n'est pas un sondage.").setEphemeral(true).queue();
                return;
            }

            List<CodeUser> userList = new ArrayList<>();
            for (MessagePoll.Answer answer : poll.getAnswers()) {
                message.retrievePollVoters(answer.getId()).queue(voters -> {
                    for (User voter : voters) {
                        CodeUser codeUser = CodeUserManager.users.get(voter.getIdLong());
                        if (!userList.contains(codeUser)) {
                            LOGs.sendLog("Ajout de " + xp + " XP à " + voter.getName() + " pour avoir voté.", DefaultLogType.XP);
                            userList.add(codeUser);
                            LevelManager.addXpAndVerify(codeUser, xp);
                        }

                    }

                });

            }

        });

        currentEvent.reply("## :white_check_mark: XP ajouté aux votants.").setEphemeral(true).queue();
    }
}
