package fr.anthonus.utils;

import java.time.Instant;

public class User {
    private long userId;

    private int xp;
    private int level;

    private Instant lastMessageTime;

    public User(long userId, int xp, int level) {
        this.userId = userId;
        this.xp = xp;
        this.level = level;

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
}
