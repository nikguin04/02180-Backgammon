package com.dtu.backgammon;

import com.dtu.backgammon.Board.Brick;

public record Move(int from, int to, MoveType movetype, Brick brick) {
    public enum MoveType {
        NORMAL,BEARINGOFF,REENTRY
    }

    public boolean isReentry() {
        return movetype == MoveType.REENTRY;
    }

    public boolean isBearingOff() {
        return movetype == MoveType.BEARINGOFF;
    }

    public int getRoll() { // Roll should fit both move out for black and white and win stuff
        return Math.abs(from-to);
    }
    @Override
    public String toString() {
        return "Move from " + from + " to " + to + " as " + movetype.name();
    }
}
