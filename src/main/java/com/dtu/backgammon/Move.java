package com.dtu.backgammon;

public class Move {
    private int from;
    private int to;

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Move from " + from + " to " + to;
    }
}