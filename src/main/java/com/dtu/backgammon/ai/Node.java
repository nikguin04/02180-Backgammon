package com.dtu.backgammon.ai;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;

public class Node {
    public final Move[] moveSequence;
    public final Board board;
    public final Brick brick;
    public int wins = 0;
    public int losses = 0;
    public int visits = 0;
    public final Node parent;

    public Node(Move[] moveSequence, Board board, Brick brick, Node parent) {
        this.moveSequence = moveSequence;
        this.board = board;
        this.brick = brick;
        this.parent = parent;
    }

    public int getTotalVisits() {
        return parent == null ? visits : parent.getTotalVisits();
    }
}
