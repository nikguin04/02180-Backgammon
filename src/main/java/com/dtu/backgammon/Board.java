package src.main.java.com.dtu.backgammon;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public enum Brick {
        WHITE,BLACK,NONE
    }
    public class BoardElement {
        Brick brick;
        int count;
        public BoardElement() {}
    }

    public List<BoardElement> board;
    public Board() {
        board = new ArrayList<BoardElement>(24);
        for (int i = 0; i < board.size(); i++) {
            board.set(i, new BoardElement());
        }

        // Test data
        board.get(0).brick = Brick.WHITE;
        board.get(0).count = 3;
    }
}
