package com.example.towersofhanoi;

public class LevelEntry implements Comparable<LevelEntry> {
    private int level;
    private String bestTime;
    private int lowestMoves;
    private String dateSet;

    public LevelEntry(int level, String bestTime, int lowestMoves, String dateSet) {
        this.level = level;
        this.bestTime = bestTime;
        this.lowestMoves = lowestMoves;
        this.dateSet = dateSet;
    }

    public int getLevel() {
        return level;
    }

    public String getBestTime() {
        return bestTime;
    }

    public int getLowestMoves() {
        return lowestMoves;
    }

    public String getDateSet() {
        return dateSet;
    }

    @Override
    public int compareTo(LevelEntry other) {
        return Integer.compare(this.level, other.level);
    }

}
