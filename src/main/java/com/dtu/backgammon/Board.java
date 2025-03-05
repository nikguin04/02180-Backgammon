package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public enum Brick {
        NONE,WHITE,BLACK
    }
    public class BoardElement {
        Brick brick;
        int count;
        public BoardElement(Brick brick, int count) { this.brick = brick; this.count = count; }
    }

    public List<BoardElement> board;
    public Board() {
        board = new ArrayList<BoardElement>(24);
        for (int i = 0; i < 24; i++) {
            board.add(i, new BoardElement(Brick.NONE, 0));
        }

        // Test data
        board.get(0).brick = Brick.WHITE;
        board.get(0).count = 3;

        board.get(2).brick = Brick.BLACK;
        board.get(2).count = 4;

        board.get(7).brick = Brick.WHITE;
        board.get(7).count = 2;

        board.get(15).brick = Brick.WHITE;
        board.get(15).count = 3;
    }

    public Brick getBrickAt(int column, int depth) {
        return (board.get(column).count > depth) ? board.get(column).brick : Brick.NONE;
    }
}
