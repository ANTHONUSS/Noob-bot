package fr.anthonus.utils;

import java.time.Instant;

public class CodeUser {
    private long userId;

    private int xp;
    private int level;

    private int nbMessagesSent;
    private int nbVoiceTimeSpent;

    private Instant lastMessageTime;

    public CodeUser(long userId, int xp, int level, int nbMessagesSent, int nbVoiceTimeSpent) {
        this.userId = userId;
        this.xp = xp;
        this.level = level;
        this.nbMessagesSent = nbMessagesSent;
        this.nbVoiceTimeSpent = nbVoiceTimeSpent;

        this.lastMessageTime = Instant.MIN;
    }

    public Instant getLastMessageTime() {
        return lastMessageTime;
    }
    public void setLastMessageTime(Instant lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public long getUserId() {
        return userId;
    }

    public int getXp() {
        return xp;
    }
    public void setXp(int xp) {
        this.xp = xp;
    }
    public void addXp(int xp) {
        this.xp += xp;
    }

    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public int getNbMessagesSent() {
        return nbMessagesSent;
    }
    public void setNbMessagesSent(int nbMessagesSent) {
        this.nbMessagesSent = nbMessagesSent;
    }
    public void addNbMessageSent(int nbToAdd) {
        this.nbMessagesSent += nbToAdd;
    }

    public int getNbVoiceTimeSpent() {
        return nbVoiceTimeSpent;
    }
    public void setNbVoiceTimeSpent(int nbVoiceTimeSpent) {
        this.nbVoiceTimeSpent = nbVoiceTimeSpent;
    }
    public void addNbVoiceTimeSpent(int nbToAdd) {
        this.nbVoiceTimeSpent += nbToAdd;
    }
}
