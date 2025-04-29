package fr.anthonus.listeners;

import fr.anthonus.LOGs;
import fr.anthonus.utils.CodeUser;
import fr.anthonus.utils.managers.CodeUserManager;
import fr.anthonus.utils.managers.DatabaseManager;
import fr.anthonus.utils.managers.LevelManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
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
            long channelId = channelJoined.getIdLong();
            voiceChannels.computeIfAbsent(channelId, id -> new VoiceChannel(channelJoined.getName()));

            VoiceChannel voiceChannel = voiceChannels.get(channelId);

            voiceChannel.numberOfUsers++;

            CodeUser codeUser = CodeUserManager.users.get(event.getMember().getUser().getIdLong());
            Member member = event.getMember();
            if (!member.getVoiceState().isMuted() && !member.getVoiceState().isDeafened()) {
                voiceChannel.activeCodeUsers.add(codeUser);

                if (!voiceChannel.isActive && voiceChannel.activeCodeUsers.size() >= 2) {
                    voiceChannel.isActive = true;
                    LOGs.sendLog("Le salon vocal " + voiceChannel.voiceChannelName + " est devenu actif", "XP");
                }

            }
        }

        if (channelLeft != null) {
            long channelId = channelLeft.getIdLong();
            VoiceChannel voiceChannel = voiceChannels.get(channelId);
            if (voiceChannel != null) {
                voiceChannel.numberOfUsers--;

                CodeUser codeUser = CodeUserManager.users.get(event.getMember().getUser().getIdLong());
                voiceChannel.activeCodeUsers.remove(codeUser);

                if (voiceChannel.isActive && voiceChannel.activeCodeUsers.size() < 2) {
                    voiceChannel.isActive = false;
                    LOGs.sendLog("Le salon vocal " + voiceChannel.voiceChannelName + " est devenu inactif", "XP");
                }

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

            if (!voiceChannel.isActive && voiceChannel.activeCodeUsers.size() >= 2) {
                voiceChannel.isActive = true;
                LOGs.sendLog("Le salon vocal " + voiceChannel.voiceChannelName + " est devenu actif", "XP");
            }

        } else {
            voiceChannel.activeCodeUsers.remove(codeUser);

            if (voiceChannel.isActive && voiceChannel.activeCodeUsers.size() < 2) {
                voiceChannel.isActive = false;
                LOGs.sendLog("Le salon vocal " + voiceChannel.voiceChannelName + " est devenu inactif", "XP");
            }

        }
    }

    private class VoiceChannel {
        public String voiceChannelName;
        public int numberOfUsers;
        public boolean isActive;

        public Set<CodeUser> activeCodeUsers = new HashSet<>();

        public ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        public VoiceChannel(String voiceChannelName) {
            this.voiceChannelName = voiceChannelName;
            this.numberOfUsers = 0;
            this.isActive = false;

            executorService.scheduleAtFixedRate(this::updateUserValues, 0, 60, TimeUnit.SECONDS);
        }

        private void updateUserValues() {
            if (isActive) {
                for (CodeUser codeUser : activeCodeUsers) {
                    codeUser.addNbVoiceTimeSpent(1);
                    DatabaseManager.updateNbVoiceTimeSpent(codeUser.getUserId(), codeUser.getNbVoiceTimeSpent());

                    LevelManager.addXpAndVerify(codeUser, LevelManager.xp_per_min_voice);
                }
                LOGs.sendLog("XP donné à tous les utilisateurs actifs du salon vocal " + voiceChannelName, "XP");
            } else {
                for (CodeUser codeUser : activeCodeUsers) {
                    codeUser.addNbVoiceTimeSpent(1);
                    DatabaseManager.updateNbVoiceTimeSpent(codeUser.getUserId(), codeUser.getNbVoiceTimeSpent());
                }
            }
        }
    }
}
