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

    // Method to get total visits (used for UCB formula)
    public int getTotalVisits() {
        int totalVisits = 0;
        Node current = this;
        while (current != null) {
            totalVisits += current.visits;
            current = current.parent;
        }
        return totalVisits;
    }
}
