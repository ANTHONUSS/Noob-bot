package fr.anthonus.listeners;

import fr.anthonus.logs.LOGs;
import fr.anthonus.commands.admin.ClearCommand;
import fr.anthonus.commands.admin.ReloadDataCommand;
import fr.anthonus.commands.admin.SetXpCommand;
import fr.anthonus.commands.admin.WinReactionCommand;
import fr.anthonus.commands.users.LeaderBoardCommand;
import fr.anthonus.commands.users.StatsCommand;
import fr.anthonus.utils.managers.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import fr.anthonus.logs.logTypes.*;

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


        switch (event.getName()) {
            // NORMAL COMMANDS
            case "stats" -> {
                long targetUserId = -1;
                if (event.getOption("user") != null) {
                    targetUserId = event.getOption("user").getAsUser().getIdLong();
                }

                StatsCommand statsCommand = new StatsCommand(event, targetUserId);
                statsCommand.run();
            }
            case "leaderboard" -> {
                LeaderBoardCommand leaderBoardCommand = new LeaderBoardCommand(event);
                leaderBoardCommand.run();
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
            case "win-reaction" -> {
                long messageId;
                try {
                    messageId = event.getOption("message-id").getAsLong();
                } catch (Exception e) {
                    event.reply("## :x: L'id entré n'est pas un id.").setEphemeral(true).queue();
                    return;
                }
                int xp = event.getOption("xp").getAsInt();


                WinReactionCommand winReactionCommand = new WinReactionCommand(event, messageId, xp);
                winReactionCommand.run();
            }
        }
        LOGs.sendLog("Commande terminée", DefaultLogType.COMMAND);

    }
}
