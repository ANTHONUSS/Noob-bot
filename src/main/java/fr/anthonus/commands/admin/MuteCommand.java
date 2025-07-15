package fr.anthonus.commands.admin;

import fr.anthonus.commands.Command;
import fr.anthonus.logs.LOGs;
import fr.anthonus.logs.logTypes.DefaultLogType;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.AdminManager;
import fr.anthonus.utils.managers.CodeUserManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;

public class MuteCommand extends Command {
    long targetUserId;
    int duration;
    String unit;

    public MuteCommand(SlashCommandInteractionEvent event, long targetUserId, int duration, String unit) {
        super(event);
        this.targetUserId = targetUserId;
        this.duration = duration;
        this.unit = unit;

        LOGs.sendLog("Commande /mute initialisée", DefaultLogType.COMMAND);
    }

    @Override
    public void run() {
        CodeUser codeUser = CodeUserManager.users.get(targetUserId);
        Member user = currentEvent.getGuild().getMemberById(targetUserId);

        if (codeUser == null) {
            currentEvent.reply("## :x: L'utilisateur n'existe pas.").setEphemeral(true).queue();
            LOGs.sendLog("L'utilisateur n'existe pas : " + targetUserId, DefaultLogType.ERROR);
            return;
        }

        if (user.isTimedOut()) {
            currentEvent.reply("## :x: L'utilisateur est déjà muet.").setEphemeral(true).queue();
            LOGs.sendLog("L'utilisateur est déjà muet : " + targetUserId, DefaultLogType.ERROR);
            return;
        }

        Duration muteDuration;
        switch (unit) {
            case "minutes" -> {
                if (duration < 1) {
                    currentEvent.reply("## :x: La durée minimale de mute est de 1 minute.").setEphemeral(true).queue();
                    LOGs.sendLog("La durée minimale de mute est de 1 minute : " + duration, DefaultLogType.ERROR);
                    return;
                } if (duration > 40_320) {
                    currentEvent.reply("## :x: La durée maximale de mute est de 40 320 minutes (28 jours).").setEphemeral(true).queue();
                    LOGs.sendLog("La durée maximale de mute est de 40 320 minutes (28 jours) : " + duration, DefaultLogType.ERROR);
                    return;
                } else {
                    muteDuration = Duration.ofMinutes(duration);
                }
            }
            case "heures" -> {
                if (duration < 1) {
                    currentEvent.reply("## :x: La durée minimale de mute est de 1 heure.").setEphemeral(true).queue();
                    LOGs.sendLog("La durée minimale de mute est de 1 heure : " + duration, DefaultLogType.ERROR);
                    return;
                } if (duration > 672) {
                    currentEvent.reply("## :x: La durée maximale de mute est de 672 heures (28 jours).").setEphemeral(true).queue();
                    LOGs.sendLog("La durée maximale de mute est de 672 heures (28 jours) : " + duration, DefaultLogType.ERROR);
                    return;
                } else {
                    muteDuration = Duration.ofHours(duration);
                }
            }
            case "jours" -> {
                if (duration < 1) {
                    currentEvent.reply("## :x: La durée minimale de mute est de 1 jour.").setEphemeral(true).queue();
                    LOGs.sendLog("La durée minimale de mute est de 1 jour : " + duration, DefaultLogType.ERROR);
                    return;
                } if (duration > 28) {
                    currentEvent.reply("## :x: La durée maximale de mute est de 28 jours.").setEphemeral(true).queue();
                    LOGs.sendLog("La durée maximale de mute est de 28 jours : " + duration, DefaultLogType.ERROR);
                    return;
                } else {
                    muteDuration = Duration.ofDays(duration);
                }
            }
            default -> {
                currentEvent.reply("## :x: Unité de temps invalide. Utilisez 'minutes', 'heures' ou 'jours'.").setEphemeral(true).queue();
                LOGs.sendLog("Unité de temps invalide : " + unit, DefaultLogType.ERROR);
                return;
            }
        }

        currentEvent.getGuild().timeoutFor(user, muteDuration).queue(
                success -> {
                    currentEvent.reply("## :white_check_mark: L'utilisateur a été muté pour " + duration + " " + unit + ".").setEphemeral(true).queue();
                    LOGs.sendLog("L'utilisateur " + user.getEffectiveName() + " a été muté pour " + duration + " " + unit, DefaultLogType.ADMIN);
                },
                failure -> {
                    currentEvent.reply("## :x: Une erreur est survenue lors du mute de l'utilisateur.").setEphemeral(true).queue();
                    LOGs.sendLog("Erreur lors du mute de l'utilisateur " + user.getEffectiveName() + ": " + failure.getMessage(), DefaultLogType.ERROR);
                }
        );

        AdminManager.sendMuteLog(codeUser, muteDuration);
    }
}
