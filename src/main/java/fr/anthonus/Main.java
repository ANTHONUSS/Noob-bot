package fr.anthonus;

import fr.anthonus.listeners.JoinEventListener;
import fr.anthonus.listeners.MessageListener;
import fr.anthonus.listeners.SlashCommandListener;
import fr.anthonus.listeners.VoiceListener;
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

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class Main {
    private static String tokenDiscord;
    public static long guildId;

    public static JDA jda;
    public static Guild guild;



    public static void main(String[] args) throws InterruptedException {
        // Load logs
        LOGs.addLogType("LOADING", 53, 74, 255);
        LOGs.addLogType("FILE_LOADING", 130, 0, 255);
        LOGs.addLogType("COMMAND", 255, 172, 53);
        LOGs.addLogType("WELCOME", 0, 143, 255);
        LOGs.addLogType("XP", 134, 55, 0);
        LOGs.addLogType("WARNING", 255, 255, 0);
        LOGs.addLogType("DEBUG", 255, 171, 247);


        Dotenv dotenv = Dotenv.configure()
                .directory("conf")
                .load();

        LOGs.sendLog("Chargement du token Discord...", "LOADING");
        tokenDiscord = dotenv.get("DISCORD_TOKEN");
        if (tokenDiscord == null || tokenDiscord.isEmpty()) {
            LOGs.sendLog("Token Discord non trouvé dans le fichier .env", "ERROR");
            return;
        } else {
            LOGs.sendLog("Token Discord chargé", "LOADING");
        }

        LOGs.sendLog("Chargement de l'identifiant du serveur...", "LOADING");
        String guildIdString = dotenv.get("SERVER_ID");
        if (guildIdString == null || guildIdString.isEmpty()) {
            LOGs.sendLog("Identifiant du serveur non trouvé dans le fichier .env", "ERROR");
            return;
        } else {
            try {
                guildId = Long.parseLong(guildIdString);
            } catch (NumberFormatException e) {
                LOGs.sendLog("Identifiant du serveur invalide dans le fichier .env", "ERROR");
                return;
            }
            LOGs.sendLog("Identifiant du serveur chargé", "LOADING");
        }


        LOGs.sendLog("Chargement du bot...", "LOADING");
        initBot();
        guild = jda.getGuildById(guildId);

        LOGs.sendLog("Chargement des paramètres...", "LOADING");
        SettingsManager.loadSettings();
        LOGs.sendLog("Paramètres chargés", "LOADING");

        LOGs.sendLog("Chargement de la base de donnée...", "LOADING");
        DatabaseManager.initDatabase();

        LOGs.sendLog("Chargement des utilisateurs...", "LOADING");
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
        LOGs.sendLog("Bot démarré", "LOADING");

        //Load the slash commands
        LOGs.sendLog("Chargement des commandes...", "LOADING");
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                // DEFAULT COMMANDS
                Commands.slash("stats", "Affiche les statistiques de soi même ou de l'utilisateur rentré en paramètre")
                        .addOption(USER, "user", "Utilisateur à afficher", false),

                Commands.slash("leaderboard", "Affiche le classement des 10 premiers utilisateurs du serveur"),

                //ADMIN COMMANDS
                Commands.slash("reload-data", "Recharge les données du bot")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

                Commands.slash("set-xp", "Donne de l'xp à un utilisateur")
                        .addOption(USER, "user", "Utilisateur à qui donner de l'xp", true)
                        .addOptions(new OptionData(INTEGER, "xp", "xp à donner", true)
                                .setRequiredRange(0, 752_500))
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                Commands.slash("clear", "Supprime un certain nombre de messages du salon.")
                        .addOptions(new OptionData(INTEGER, "nombre", "nombre de messages à supprimer", true)
                                .setRequiredRange(1, 100))
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))

        );
        commands.queue();
        LOGs.sendLog("Commandes chargées", "LOADING");
    }
}