package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.dtu.backgammon.player.AI;
import com.dtu.backgammon.player.Human;
import com.dtu.backgammon.player.Player;

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
    public List<Player> players = new ArrayList<Player>();
    public Board() throws Exception {
        board = new ArrayList<BoardElement>(24);
        for (int i = 0; i < 24; i++) {
            board.add(i, new BoardElement(Brick.NONE, 0));
        }
        
        board.get(0).brick = Brick.WHITE;
        board.get(0).count = 3;

        board.get(2).brick = Brick.BLACK;
        board.get(2).count = 4;

        board.get(7).brick = Brick.WHITE;
        board.get(7).count = 2;

        board.get(14).brick = Brick.WHITE;
        board.get(14).count = 3;

        setupPlayers();
    }

    private void setupPlayers() throws Exception {
        // Initialize players
        Pattern playerPattern = Pattern.compile("(human|ai)", Pattern.CASE_INSENSITIVE);
        for (Brick brick : Brick.class.getEnumConstants()) {
            if (brick.name() == "NONE") { continue; } // Do not initialize a player as no brick

            System.out.println("Please choose a player for " + brick.name() + " (Human / AI):");
            while (!App.scanner.hasNext(playerPattern)) {
                App.scanner.next(); // remove current input in scanner buffer
                System.out.println("Please choose either (Human / AI)");
            }
            String playerType = App.scanner.next(playerPattern);

            switch (playerType.toLowerCase()) {
                case "human":
                    players.add(new Human(brick));
                    break;
                case "ai":
                    players.add(new AI(brick));
                    break;
                default:
                    throw new Exception("Failed to match input for player type: " + playerType);
            }
        }
    }

    public Brick getBrickAt(int column, int depth) {
        return (board.get(column).count > depth) ? board.get(column).brick : Brick.NONE;
    }
}
