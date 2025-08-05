package fr.anthonus.commands.users;

import fr.anthonus.commands.Command;
import fr.anthonus.logs.LOGs;
import fr.anthonus.logs.logTypes.DefaultLogType;
import fr.anthonus.utils.managers.SettingsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class InfoCommand extends Command {

    public InfoCommand(SlashCommandInteractionEvent event) {
        super(event);

        LOGs.sendLog("Commande /info initialisée", DefaultLogType.COMMAND);
    }

    @Override
    public void run() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Bot officiel de Noob Nation !");
        embed.setThumbnail(currentEvent.getJDA().getSelfUser().getAvatarUrl());

        embed.addField("Ping", "`" + currentEvent.getJDA().getGatewayPing() + " ms`", true);
        embed.addField("Version", "`" + SettingsManager.getVersion() + "`", true);

        StringBuilder credits = new StringBuilder();
        credits.append("Développé par ANTHONUS, tous droits réservés.").append("\n");
        credits.append("[Site web](https://anthonus.fr)").append("\n");
        credits.append("[GitHub](https://github.com/ANTHONUSS)").append("\n");
        credits.append("Photo de profil du bot et du serveur par cookyalfild.");
        embed.addField("Crédits du bot", credits.toString(), false);

        currentEvent.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
