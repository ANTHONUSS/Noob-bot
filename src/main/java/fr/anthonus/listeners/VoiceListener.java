package fr.anthonus.listeners;

import fr.anthonus.LOGs;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.CodeUserManager;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.LevelManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoiceListener extends ListenerAdapter {
    private final Map<Long, VoiceChannel> voiceChannels = new HashMap<>();

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        AudioChannelUnion channelJoined = event.getChannelJoined();
        AudioChannelUnion channelLeft = event.getChannelLeft();
        if (channelJoined != null) {
            voiceChannels.computeIfAbsent(channelJoined.getIdLong(), id -> new VoiceChannel());

            VoiceChannel voiceChannel = voiceChannels.get(channelJoined.getIdLong());

            voiceChannel.numberOfUsers++;

            CodeUser codeUser = CodeUserManager.users.get(event.getMember().getUser().getIdLong());
            Member member = event.getMember();
            if (!member.getVoiceState().isMuted() && !member.getVoiceState().isDeafened()) {
                voiceChannel.activeCodeUsers.add(codeUser);
            }
        }

        if (channelLeft != null) {
            VoiceChannel voiceChannel = voiceChannels.get(channelLeft.getIdLong());
            if (voiceChannel != null) {
                voiceChannel.numberOfUsers--;

                CodeUser codeUser = CodeUserManager.users.get(event.getMember().getUser().getIdLong());
                voiceChannel.activeCodeUsers.remove(codeUser);
            }
        }

        voiceChannels.entrySet().removeIf(entry -> entry.getValue().numberOfUsers == 0);
    }

    @Override
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        CodeUser codeUser = CodeUserManager.users.get(event.getMember().getUser().getIdLong());
        Member member = event.getMember();
        if (event.getVoiceState().getChannel() == null) {
            return;
        }
        VoiceChannel voiceChannel = voiceChannels.get(event.getVoiceState().getChannel().getIdLong());

        if (!member.getVoiceState().isMuted() && !member.getVoiceState().isDeafened()) {
            voiceChannel.activeCodeUsers.add(codeUser);
        } else {
            voiceChannel.activeCodeUsers.remove(codeUser);
        }
    }

    private class VoiceChannel {
        public int numberOfUsers;

        public Set<CodeUser> activeCodeUsers = new HashSet<>();

        public ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        public VoiceChannel() {
            this.numberOfUsers = 0;

            executorService.scheduleAtFixedRate(this::giveXP, 0, 60, TimeUnit.SECONDS);
        }

        private void giveXP() {
            if (activeCodeUsers.size() >= 2) {
                for (CodeUser codeUser : activeCodeUsers) {
                    long userId = codeUser.getUserId();

                    int levelBefore = LevelManager.getLevelFromXP(codeUser.getXp());

                    codeUser.addXp(LevelManager.xp_per_min_voice);
                    DatabaseManager.updateXp(userId, codeUser.getXp());

                    int levelAfter = LevelManager.getLevelFromXP(codeUser.getXp());
                    if (levelBefore != levelAfter) {
                        codeUser.setLevel(levelAfter);
                        DatabaseManager.updateLevel(userId, levelAfter);
                        LevelManager.sendLevelUpMessage(userId, levelAfter);
                        LevelManager.checkAndUpdateUserRole(userId, levelAfter);
                    }
                }
            }
        }
    }
}
