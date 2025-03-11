package com.dtu.backgammon.player;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;

public abstract class Player {
    protected Brick brick;
    public Player(Brick brick) {
        this.brick = brick;
    }

    public abstract Move getMove();
    public abstract String getName();
}
