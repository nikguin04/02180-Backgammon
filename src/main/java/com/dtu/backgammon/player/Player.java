package com.dtu.backgammon.player;

import com.dtu.backgammon.Board.Brick;

public abstract class Player {
    protected Brick brick;
    public Player(Brick brick) {
        this.brick = brick;
    }

    public abstract void getMove();
    public abstract String getName();
}
