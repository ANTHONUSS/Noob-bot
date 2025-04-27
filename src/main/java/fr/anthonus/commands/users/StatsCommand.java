package fr.anthonus.commands.users;

import fr.anthonus.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StatsCommand extends Command {
    public StatsCommand(SlashCommandInteractionEvent event) {
        super(event);

        LOGs.sendLog("Commande /stats initialisée", "COMMAND");
    }

    @Override
    public void run() {
        long userId = currentEvent.getUser().getIdLong();


    }

}
