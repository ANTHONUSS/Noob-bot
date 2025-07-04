package fr.anthonus.utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CodeUser {
    private final long userId;

    private int xp;
    private int level;

    private int nbMessagesSent;
    private int nbVoiceTimeSpent;

    private Instant lastMessageTime;

    private final List<Long> MESSAGES_ID = new ArrayList<>();
    private final List<String> MESSAGES_STRING = new ArrayList<>();

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
    public void addNbMessageSent(int nbToAdd) {
        this.nbMessagesSent += nbToAdd;
    }

    public int getNbVoiceTimeSpent() {
        return nbVoiceTimeSpent;
    }
    public void addNbVoiceTimeSpent(int nbToAdd) {
        this.nbVoiceTimeSpent += nbToAdd;
    }

    public List<Long> getMessagesId() {
        return MESSAGES_ID;
    }
    public int getNbMessageSentInARow() {
        return MESSAGES_ID.size();
    }
    public void addMessageSentInARow(long messageID) {
        MESSAGES_ID.add(messageID);
    }
    public void resetMessagesSentInARow() {
        MESSAGES_ID.clear();
    }

    public List<String> getMessagesString() {
        return MESSAGES_STRING;
    }
    public void addMessageString(String message) {
        MESSAGES_STRING.add(message);
    }
    public void resetMessagesString() {
        MESSAGES_STRING.clear();
    }
}
