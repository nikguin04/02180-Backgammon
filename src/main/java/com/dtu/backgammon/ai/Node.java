package com.dtu.backgammon.ai;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.player.Player;

public class Node {
    private Move[] moveSequence;  // Sequence of moves
    private Board board;
    private Brick brick;
    private int wins;
    private int losses;
    private int simulations;

    public Node(Move[] moveSequence, Board board, Brick brick) {
        this.moveSequence = moveSequence;
        this.board = board;
        this.brick = brick;
        this.wins = 0;
        this.losses = 0;
        this.simulations = 0;
    }

    public Move[] getMoveSequence() {
        return moveSequence;
    }

    public Board getBoard() {
        return board;
    }

    public Brick getBrick() {
        return brick;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        wins++;
        simulations++;
    }

    public void incrementLosses() {
        losses++;
        simulations++;
    }
}


