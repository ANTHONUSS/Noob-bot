package fr.anthonus.utils;

public class User {
    private long userId;

    private int xp;
    private int level;

    public User(long userId, int xp, int level) {
        this.userId = userId;
        this.xp = xp;
        this.level = level;
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

    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
}
