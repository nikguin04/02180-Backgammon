package com.dtu.backgammon;


public record Move(int from, int to, MoveType movetype) {
    public enum MoveType {
        NORMAL,BEARINGOFF,REENTRY
    }

    public boolean isReentry() {
        return movetype == MoveType.REENTRY;
    }

    public boolean isBearingOff() {
        return movetype == MoveType.BEARINGOFF;
    }

    @Override
    public String toString() {
        return "Move from " + from + " to " + to;
    }
}
