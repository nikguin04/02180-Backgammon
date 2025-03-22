package com.dtu.backgammon.player;

import java.util.List;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;

public abstract class Player {
    public int totalMoveValue = 0;
    final public Brick brick;
    public Player(Brick brick) {
        this.brick = brick;
    }

    public abstract Move[] getMove(Board board, List<Integer> roll);
    public abstract String getName();
}
