package fr.anthonus;

import fr.anthonus.listeners.JoinEventListener;
import fr.anthonus.listeners.MessageListener;
import fr.anthonus.listeners.SlashCommandListener;
import fr.anthonus.listeners.VoiceListener;
import fr.anthonus.logs.LOGs;
import fr.anthonus.logs.logTypes.*;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.SettingsManager;
import fr.anthonus.utils.managers.CodeUserManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.Scanner;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class Main {
    private static String tokenDiscord;
    public static long guildId;

    public static JDA jda;
    public static Guild guild;


    public static void main(String[] args) throws InterruptedException {
        init();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "exit" -> {
                    LOGs.sendLog("Arrêt du bot...", DefaultLogType.LOADING);
                    jda.shutdownNow();

                    System.exit(0);
                }
                case "reload" -> {
                    LOGs.sendLog("Rechargement du bot...", DefaultLogType.LOADING);
                    jda.shutdown();

                    init();
                    LOGs.sendLog("Bot rechargé", DefaultLogType.LOADING);
                }
            }
        }
    }

    private static void init() throws InterruptedException {
        Dotenv dotenv = Dotenv.configure()
                .directory("conf")
                .load();

        LOGs.sendLog("Chargement du token Discord...", DefaultLogType.LOADING);
        tokenDiscord = dotenv.get("DISCORD_TOKEN");
        if (tokenDiscord == null || tokenDiscord.isEmpty()) {
            LOGs.sendLog("Token Discord non trouvé dans le fichier .env", DefaultLogType.ERROR);
            return;
        } else {
            LOGs.sendLog("Token Discord chargé", DefaultLogType.LOADING);
        }

        LOGs.sendLog("Chargement de l'identifiant du serveur...", DefaultLogType.LOADING);
        String guildIdString = dotenv.get("SERVER_ID");
        if (guildIdString == null || guildIdString.isEmpty()) {
            LOGs.sendLog("Identifiant du serveur non trouvé dans le fichier .env", DefaultLogType.ERROR);
            return;
        } else {
            try {
                guildId = Long.parseLong(guildIdString);
            } catch (NumberFormatException e) {
                LOGs.sendLog("Identifiant du serveur invalide dans le fichier .env", DefaultLogType.ERROR);
                return;
            }
            LOGs.sendLog("Identifiant du serveur chargé", DefaultLogType.LOADING);
        }


        LOGs.sendLog("Chargement du bot...", DefaultLogType.LOADING);
        initBot();
        guild = jda.getGuildById(guildId);
        if (guild == null) {
            LOGs.sendLog("Le bot n'est pas dans le serveur avec l'ID " + guildId, DefaultLogType.ERROR);
            return;
        } else {
            LOGs.sendLog("Bot connecté au serveur " + guild.getName(), DefaultLogType.LOADING);
        }

        LOGs.sendLog("Chargement des paramètres...", DefaultLogType.LOADING);
        SettingsManager.loadSettings();
        LOGs.sendLog("Paramètres chargés", DefaultLogType.LOADING);

        LOGs.sendLog("Chargement de la base de donnée...", DefaultLogType.LOADING);
        DatabaseManager.initDatabase();

        LOGs.sendLog("Chargement des utilisateurs...", DefaultLogType.LOADING);
        CodeUserManager.loadUsers();
    }

    private static void initBot() throws InterruptedException {
        //Load the bot
        jda = JDABuilder.createDefault(tokenDiscord)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new JoinEventListener())
                .addEventListeners(new SlashCommandListener())
                .addEventListeners(new MessageListener())
                .addEventListeners(new VoiceListener())
                .build();

        jda.awaitReady();
        LOGs.sendLog("Bot démarré", DefaultLogType.LOADING);

        //Load the slash commands
        LOGs.sendLog("Chargement des commandes...", DefaultLogType.LOADING);
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                // DEFAULT COMMANDS
                Commands.slash("stats", "Affiche les statistiques de soi même ou de l'utilisateur rentré en paramètre")
                        .addOption(USER, "user", "Utilisateur à afficher", false),

                Commands.slash("leaderboard", "Affiche le classement des 10 premiers utilisateurs du serveur"),

                Commands.slash("info", "Affiche les informations du bot"),

                //ADMIN COMMANDS
                Commands.slash("reload-data", "Recharge les données du bot")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

                Commands.slash("mute", "Mute un utilisateur pour une durée déterminée")
                        .addOption(USER, "utilisateur", "Utilisateur à mute", true)
                        .addOptions(new OptionData(INTEGER, "durée", "Durée du mute", true))
                        .addOptions(new OptionData(STRING, "unité", "Unité de la durée", true)
                                .addChoice("Minutes", "minutes")
                                .addChoice("Heures", "heures")
                                .addChoice("Jours", "jours"))
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

                Commands.slash("clear", "Supprime un certain nombre de messages du salon.")
                        .addOptions(new OptionData(INTEGER, "nombre", "nombre de messages à supprimer", true)
                                .setRequiredRange(1, 100))
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)),

                Commands.slash("set-xp", "Donne de l'xp à un utilisateur")
                        .addOption(USER, "user", "Utilisateur à qui donner de l'xp", true)
                        .addOptions(new OptionData(INTEGER, "xp", "xp à donner", true)
                                .setRequiredRange(0, 752_500))
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

                Commands.slash("win-reaction", "Donne le nombre d'xp à tous les utilisateurs qui ont coché une réaction au message")
                        .addOption(STRING, "message-id", "ID du message", true)
                        .addOption(INTEGER, "xp", "xp à donner", true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

                Commands.slash("set-user-score", "Modifie le score d'un utilisateur")
                        .addOption(USER, "user", "Utilisateur à modifier", true)
                        .addOptions(new OptionData(INTEGER, "score", "score de l'utilisateur", true)
                                        .setRequiredRange(-5, 5))
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))


        );
        commands.queue();
        LOGs.sendLog("Commandes chargées", DefaultLogType.LOADING);
    }
}