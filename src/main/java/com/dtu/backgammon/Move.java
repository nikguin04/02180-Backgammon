package com.dtu.backgammon;

public record Move(int from, int to) {
    public boolean isReentry() {
        return from == -1;
    }

    public boolean isBearingOff() {
        return to == -1;
    }

    @Override
    public String toString() {
        return "Move from " + from + " to " + to;
    }
}
