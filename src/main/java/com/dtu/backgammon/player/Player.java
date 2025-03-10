package com.dtu.backgammon.player;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;

public abstract class Player {
    Brick brick;
    Board board;
    public Player(Brick brick, Board board) {
        this.brick = brick;
        this.board = board;
    }

    public abstract void getMove();
    public abstract String getName();
}
