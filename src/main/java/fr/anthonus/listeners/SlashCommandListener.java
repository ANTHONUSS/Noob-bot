package fr.anthonus.listeners;

import fr.anthonus.LOGs;
import fr.anthonus.commands.admin.ClearCommand;
import fr.anthonus.commands.admin.ReloadDataCommand;
import fr.anthonus.commands.admin.SetXpCommand;
import fr.anthonus.commands.users.StatsCommand;
import fr.anthonus.utils.managers.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static fr.anthonus.Main.*;

public class SlashCommandListener extends ListenerAdapter {


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        TextChannel commandChannel = guild.getTextChannelById(SettingsManager.commandsChannel);
        if (!event.getChannel().equals(commandChannel) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Veuillez utiliser les commandes dans le salon <#" + SettingsManager.commandsChannel + ">.").setEphemeral(true).queue();
            return;
        }

        if (!event.isFromGuild()) {
            event.reply("Vous ne pouvez pas utiliser les commandes dans les mp du bot.").setEphemeral(true).queue();
            return;
        }


        switch (event.getName()){
            // NORMAL COMMANDS
            case "stats" -> {
                long targetUserId = -1;
                if (event.getOption("user") != null) {
                    targetUserId = event.getOption("user").getAsUser().getIdLong();
                }

                StatsCommand statsCommand = new StatsCommand(event, targetUserId);
                statsCommand.run();
            }

            //ADMIN COMMANDS
            case "reload-data" -> {
                ReloadDataCommand reloadDataCommand = new ReloadDataCommand(event);
                reloadDataCommand.run();
            }
            case "set-xp" -> {
                long targetUserId = event.getOption("user").getAsUser().getIdLong();
                int xp = event.getOption("xp").getAsInt();

                SetXpCommand setXpCommand = new SetXpCommand(event, targetUserId, xp);
                setXpCommand.run();
            }
            case "clear" -> {
                int count = event.getOption("nombre").getAsInt();

                ClearCommand clearCommand = new ClearCommand(event, count);
                clearCommand.run();
            }
        }
        LOGs.sendLog("Commande terminée", "COMMAND");

    }
}
