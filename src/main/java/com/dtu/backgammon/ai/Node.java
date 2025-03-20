package com.dtu.backgammon.ai;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;
public class Node {
    private Move[] moveSequence;
    private Board board;
    private Brick brick;
    private int wins = 0;
    private int losses = 0;
    private int visits = 0;
    private Node parent;

    public Node(Move[] moveSequence, Board board, Brick brick, Node parent) {
        this.moveSequence = moveSequence;
        this.board = board;
        this.brick = brick;
        this.parent = parent;
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementLosses() {
        losses++;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getVisits() {
        return visits;
    }

    public void incrementVisits() {
        visits++;
    }

    public Node getParent() {
        return parent;
    }

    public int getTotalVisits() {
        return parent == null ? visits : parent.getTotalVisits();
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
}
