package fr.anthonus.commands.admin;

import fr.anthonus.logs.LOGs;
import fr.anthonus.commands.Command;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.CodeUserManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import fr.anthonus.logs.logTypes.*;

public class ReloadDataCommand extends Command {
    public ReloadDataCommand(SlashCommandInteractionEvent event) {
        super(event);

        LOGs.sendLog("Commande /reload-data initialisée", DefaultLogType.COMMAND);
    }

    @Override
    public void run() {
        DatabaseManager.initDatabase(); // un peu inutile mais oklm

        CodeUserManager.users.clear();
        CodeUserManager.loadUsers();

        currentEvent.reply("Données rechargées avec succès !").setEphemeral(true).queue();

        LOGs.sendLog("Données du bot rechargées", DefaultLogType.COMMAND);
    }
}
