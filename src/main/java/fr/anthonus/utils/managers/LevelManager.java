package fr.anthonus.utils.managers;

import fr.anthonus.logs.LOGs;
import fr.anthonus.utils.CodeUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import fr.anthonus.logs.logTypes.*;

import static fr.anthonus.Main.*;

public class LevelManager {
    private static int a = 75;
    private static int b = 25;

    public static int xp_per_msg = 10;
    public static int xp_per_min_voice = 10;
    public static int maxXp = 752500;

    /**
     * Les niveaux à atteindre pour chaque palier (voir excel pour référence)
     */
    private static int[] paliersLevels = {0, 2, 5, 8, 20, 29, 49, 64, 79, 89, 100};
    private static long[] paliersRoles = {1364222563546566696L, 1363255586464076036L, 1363258951021625404L, 1363258861607452900L, 1363258980515840210L, 1363259013277417572L, 1363259050497802365L,
            1363259085163860110L, 1363259115677421729L, 1363259148585795806L, 1363259177828487310L};

    public static int getXPforLevel(int level) {
        return 75 * level * level + 25 * level;
    }

    public static int getLevelFromXP(int xp) {
        double c = -xp;

        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0)
            return -1; // askip c'est impossible mais on sait jamais

        double sqrtDiscriminant = Math.sqrt(discriminant);

        double level = (-b + sqrtDiscriminant) / (2 * a);

        return (int) Math.floor(level);
    }

    public static int getXPForLevelUp(int xp) {
        return getXPforLevel(getLevelFromXP(xp) + 1) - xp;
    }

    public static int getLevelsForNextPalier(int level) {
        for (int i = 0; i < paliersLevels.length; i++) {
            if (level < paliersLevels[i]) {
                return paliersLevels[i] - level;
            }
        }
        return -1;
    }

    public static void checkAndUpdateUserRole(long userId, int userLevel) {
        // Trouver le rôle correspondant au niveau de l'utilisateur
        Role correctRole = null;
        for (int i = 0; i < paliersLevels.length; i++) {
            if (userLevel >= paliersLevels[i]) {
                correctRole = guild.getRoleById(paliersRoles[i]);
            } else {
                break;
            }
        }

        if (correctRole == null) {
            LOGs.sendLog("Aucun rôle trouvé pour le niveau " + userLevel, DefaultLogType.WARNING);
            return; // Aucun rôle trouvé pour ce niveau
        }

        Member member = guild.getMemberById(userId);
        // Vérifier si l'utilisateur a déjà le rôle correct
        for (long roleId : paliersRoles) {
            Role role = guild.getRoleById(roleId);
            if (role != null && member.getRoles().contains(role)) {
                if (role.getIdLong() == correctRole.getIdLong()) {
                    return; // L'utilisateur a déjà le rôle correct
                }
            }
        }

        // Ajouter le rôle correct à l'utilisateur
        guild.addRoleToMember(member, correctRole).queue();

        // Supprimer tous les autres rôles de palier de l'utilisateur
        for (long roleId : paliersRoles) {
            Role role = guild.getRoleById(roleId);
            if (role != null && member.getRoles().contains(role) && role.getIdLong() != correctRole.getIdLong()) {
                guild.removeRoleFromMember(member, role).queue();
            }
        }

        // Envoyer un message de félicitations
        if (correctRole.getIdLong() != paliersRoles[0])
            sendPalierChangeMessage(userId, correctRole);

        LOGs.sendLog("L'utilisateur " + jda.retrieveUserById(userId).complete().getName() + " est monté au palier " + correctRole.getName(), DefaultLogType.XP);

    }

    public static void sendLevelUpMessage(long userId, int level) {
        User user = jda.retrieveUserById(userId).complete();
        TextChannel channel = guild.getTextChannelById(SettingsManager.levelInfoChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Nouveau niveau atteint !");
        embed.setDescription("Bravo " + user.getName() + ", tu es maintenant au niveau " + level + " !");
        embed.setThumbnail(user.getAvatarUrl());
        embed.setColor(getPalier(level).getColor());

        channel.sendMessage(user.getAsMention())
                .setEmbeds(embed.build())
                .queue();
    }

    public static void sendPalierChangeMessage(long userId, Role role) {
        User user = jda.retrieveUserById(userId).complete();
        TextChannel channel = guild.getTextChannelById(SettingsManager.levelInfoChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Nouveau palier atteint !");
        embed.setDescription("Bravo " + user.getName() + ", tu as atteint le palier **" + role.getName() + "** !");
        embed.setThumbnail(user.getAvatarUrl());
        embed.setColor(role.getColor());

        channel.sendMessage(user.getAsMention())
                .setEmbeds(embed.build())
                .queue();
    }

    public static Role getPalier(int level) {
        for (int i = 0; i < paliersLevels.length; i++) {
            if (level >= paliersLevels[i] && (i == paliersLevels.length - 1 || level < paliersLevels[i + 1])) {
                return guild.getRoleById(paliersRoles[i]);
            }
        }

        return null;
    }

    public static void addXpAndVerify(CodeUser codeUser, int xp) {
        // Vérification si l'utilisateur a déjà atteint le maximum d'XP
        if (codeUser.getXp() >= maxXp) {
            return;
        }

        long userId = codeUser.getUserId();

        // Gestion de passage de niveau et/ou palier
        int levelBefore = getLevelFromXP(codeUser.getXp());

        codeUser.addXp(xp);
        DatabaseManager.updateXp(userId, codeUser.getXp());

        int levelAfter = getLevelFromXP(codeUser.getXp());

        if (levelBefore != levelAfter) {
            LOGs.sendLog("L'utilisateur " + jda.retrieveUserById(userId).complete().getName() + " est passé au niveau " + levelAfter, DefaultLogType.XP);
            codeUser.setLevel(levelAfter);
            DatabaseManager.updateLevel(userId, levelAfter);
            sendLevelUpMessage(userId, levelAfter);

            if (levelAfter % 5 == 0) {
                // Si l'utilisateur passe un niveau divisible par 5, on lui donne 2 points de score
                codeUser.addScore(2);
                DatabaseManager.updateScore(userId, codeUser.getScore());
            }

            checkAndUpdateUserRole(userId, levelAfter);
        }
    }
}
