package fr.anthonus.commands.admin;

import fr.anthonus.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.UserManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ReloadDataCommand extends Command {
    public ReloadDataCommand(SlashCommandInteractionEvent event) {
        super(event);

        LOGs.sendLog("Commande /reload-data initialisée", "COMMAND");
    }

    @Override
    public void run() {
        DatabaseManager.initDatabase(); // un peu inutile mais oklm

        UserManager.users.clear();
        UserManager.loadUsers();

        currentEvent.reply("Données rechargées avec succès !").setEphemeral(true).queue();

        LOGs.sendLog("Données du bot rechargées", "COMMAND");
    }
}
