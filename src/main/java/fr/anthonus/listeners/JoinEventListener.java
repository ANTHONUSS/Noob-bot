package fr.anthonus.listeners;

import fr.anthonus.logs.LOGs;
import fr.anthonus.utils.*;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.LevelManager;
import fr.anthonus.utils.managers.SettingsManager;
import fr.anthonus.utils.managers.CodeUserManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.concurrent.ThreadLocalRandom;
import fr.anthonus.logs.logTypes.*;

import static fr.anthonus.Main.*;

public class JoinEventListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        LOGs.sendLog("Un nouvel utilisateur a rejoint le serveur : " + event.getUser().getName(), DefaultLogType.WELCOME);

        sendWelcomeMessage(event);

        addUserToDatabase(event);
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        LOGs.sendLog("Un utilisateur a quitté le serveur : " + event.getUser().getName(), DefaultLogType.WELCOME);

        sendGoodbyeMessage(event);

        removeUserFromMemory(event);
    }

    @Override
    public void onGuildMemberUpdatePending(GuildMemberUpdatePendingEvent event) {
        if (!event.getNewPending()) {
            // L'utilisateur a accepté les règles et est maintenant un membre actif
            // Vous pouvez maintenant accéder à ses rôles et autres informations
            long userId = event.getUser().getIdLong();
            int userLevel = CodeUserManager.users.get(userId).getLevel();
            LevelManager.checkAndUpdateUserRole(userId, userLevel);
            LOGs.sendLog("L'utilisateur " + event.getUser().getName() + " a accepté les règles et est maintenant un membre actif.", DefaultLogType.WELCOME);
        }
    }

    private void sendWelcomeMessage(GuildMemberJoinEvent event) {
        TextChannel joinChannel = guild.getTextChannelById(SettingsManager.arrivalsChannel);

        EmbedBuilder embedBuilder = new EmbedBuilder();

        String title = null;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int rand = random.nextInt(0, 9);
        switch (rand) {
            case 0 -> title = "Bienvenue parmi nous !";
            case 1 -> title = "Ravi de te voir ici !";
            case 2 -> title = "Hello et bienvenue !";
            case 3 -> title = "Salut à toi, nouvel arrivant !";
            case 4 -> title = "Bienvenue dans notre communauté !";
            case 5 -> title = "Content de te voir ici !";
            case 6 -> title = "Un grand bienvenue à toi !";
            case 7 -> title = "Heureux de t'accueillir parmi nous !";
            case 8 -> title = "Bienvenue, fais comme chez toi !";
            case 9 -> title = "Salut et bienvenue dans le serveur !";
        }

        String description = "Salut " + event.getUser().getName() + " ! Installe toi confortablement et n'hésite pas à aller voir le <id:guide> !\n" +
                "N'oublie surtout pas d'aller lire l'onglet <#1363949593187848343> pour être conscient des fonctionnalités du serveur !\n" +
                "(il y a pas mal de choses à lire, donc prends ta meilleur tasse de thé et prends ton temps de tout lire :upside_down: !).";

        //tests embeds
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setFooter("profite bien du serveur !");
        embedBuilder.setThumbnail(event.getUser().getAvatarUrl());

        joinChannel.sendMessage(event.getUser().getAsMention())
                .setEmbeds(embedBuilder.build())
                .queue();
    }

    private void addUserToDatabase(GuildMemberJoinEvent event) {
        long userId = event.getUser().getIdLong();
        CodeUser codeUser = DatabaseManager.loadUser(userId);
        if (codeUser == null) {
            codeUser = new CodeUser(userId, 0, 0, 0, 0);
            DatabaseManager.saveUser(codeUser);
            CodeUserManager.users.put(userId, codeUser);

            LOGs.sendLog("Nouvel utilisateur ajouté à la base de données et chargé en mémoire : " + event.getUser().getName(), DefaultLogType.FILE_LOADING);
        } else {
            LOGs.sendLog("Utilisateur déjà présent dans la base de données : " + event.getUser().getName() + ", chargement en mémoire...", DefaultLogType.FILE_LOADING);
            CodeUserManager.users.put(userId, codeUser);
            LOGs.sendLog("Utilisateur chargé en mémoire : " + event.getUser().getName(), DefaultLogType.FILE_LOADING);
        }

    }

    private void sendGoodbyeMessage(GuildMemberRemoveEvent event) {
        TextChannel joinChannel = guild.getTextChannelById(SettingsManager.arrivalsChannel);

        EmbedBuilder embedBuilder = new EmbedBuilder();

        String title = event.getUser().getName() + " a quitté le serveur !";

        String description = null;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int rand = random.nextInt(0, 9);
        switch (rand) {
            case 0 -> description = "Dommage de te voir partir...";
            case 1 -> description = "Tu vas nous manquer !";
            case 2 -> description = "Au revoir et bonne continuation !";
            case 3 -> description = "Triste de te voir quitter le serveur...";
            case 4 -> description = "Merci d'avoir été parmi nous !";
            case 5 -> description = "Bonne route à toi !";
            case 6 -> description = "Au plaisir de te revoir un jour !";
            case 7 -> description = "Prends soin de toi, à bientôt !";
            case 8 -> description = "Tu seras toujours le bienvenu ici !";
            case 9 -> description = "Adieu et bonne chance pour la suite !";
        }


        //tests embeds
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setThumbnail(event.getUser().getAvatarUrl());

        joinChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void removeUserFromMemory(GuildMemberRemoveEvent event) {
        long userId = event.getUser().getIdLong();
        CodeUserManager.users.remove(userId);
        LOGs.sendLog("Utilisateur supprimé de la mémoire : " + event.getUser().getName(), DefaultLogType.FILE_LOADING);
    }
}
