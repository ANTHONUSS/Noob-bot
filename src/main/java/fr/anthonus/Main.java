package fr.anthonus;

import fr.anthonus.listeners.JoinEventListener;
import fr.anthonus.utils.DatabaseManager;
import fr.anthonus.utils.SettingsManager;
import fr.anthonus.utils.UserManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class Main {
    private static String tokenDiscord;
    public static long guildId;

    public static JDA jda;



    public static void main(String[] args) throws InterruptedException {
        // Load logs
        LOGs.addLogType("LOADING", 53, 74, 255);
        LOGs.addLogType("FILE_LOADING", 130, 0, 255);
        LOGs.addLogType("COMMAND", 255, 172, 53);
        LOGs.addLogType("WELCOME", 0, 143, 255);
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

        LOGs.sendLog("Chargement des paramètres...", "LOADING");
        SettingsManager.loadSettings();
        LOGs.sendLog("Paramètres chargés", "LOADING");


        LOGs.sendLog("Chargement de la base de donnée...", "LOADING");
        DatabaseManager.initDatabase();

        LOGs.sendLog("Chargement des utilisateurs...", "LOADING");
        UserManager.loadUsers();

    }

    private static void initBot() throws InterruptedException {
        //Load the bot
        jda = JDABuilder.createDefault(tokenDiscord)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .addEventListeners(new JoinEventListener())
                .build();

        jda.awaitReady();
        LOGs.sendLog("Bot démarré", "LOADING");

        //Load the slash commands
        LOGs.sendLog("Chargement des commandes...", "LOADING");
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                // DEFAULT COMMANDS
                Commands.slash("stats", "Affiche les statistiques de soi même ou de l'utilisateur rentré en paramètre")
                        .addOption(USER, "user", "Utilisateur à afficher", false)

        );
        commands.queue();
        LOGs.sendLog("Commandes chargées", "LOADING");
    }
}