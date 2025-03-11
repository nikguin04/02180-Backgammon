package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.dtu.backgammon.ai.AI;
import com.dtu.backgammon.player.Human;
import com.dtu.backgammon.player.Player;

public class Board {

    int winTrayWhite = 0;

    int winTrayBlack = 0;

    int barWhite = 0;

    int barBlack = 0;

    int blackHomeBoard = 0;

    int whiteHomeBoard = 0;

    int maxBlackHomeBoard =  15;

    int maxWhiteHomeBoard = 15;

    public enum Brick {
        NONE, WHITE, BLACK
    }
    public class BoardElement {
        public Brick brick;
        public int count;
        public BoardElement(Brick brick, int count) { this.brick = brick; this.count = count; }
        @Override
        public BoardElement clone() { return new BoardElement(brick, count); }
    }

    public List<BoardElement> board;
    public List<Player> players = new ArrayList<>();

    public static int WHITE_START = -1;
    public static int BLACK_START = 24;

    public Board() throws Exception {
        board = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            board.add(i, new BoardElement(Brick.NONE, 0));
        }

        setupPlayers();
        setupStandardBoard();
    }
    public Board(List<BoardElement> board, List<Player> players) {
        this.board = board; this.players = players;
    }

    private void setupStandardBoard() {
        setColumn(0, Brick.WHITE, 2);
        setColumn(11, Brick.WHITE, 5);
        setColumn(16, Brick.WHITE, 3);
        setColumn(18, Brick.WHITE, 5);

        setColumn(23, Brick.BLACK, 2);
        setColumn(12, Brick.BLACK, 5);
        setColumn(7, Brick.BLACK, 3);
        setColumn(5, Brick.BLACK, 5);
    }
    private void setColumn(int column, Brick player, int count) {
        board.get(column).brick = player;
        board.get(column).count = count;
    }


    private void setupPlayers() throws Exception {
        // Initialize players
        Pattern playerPattern = Pattern.compile("(human|ai)", Pattern.CASE_INSENSITIVE);
        for (Brick brick : Brick.class.getEnumConstants()) {
            if (brick == Brick.NONE) { continue; } // Do not initialize a player as no brick

            System.out.println("Please choose a player for " + brick.name() + " (Human / AI):");
            while (!App.scanner.hasNext(playerPattern)) {
                App.scanner.next(); // remove current input in scanner buffer
                System.out.println("Please choose either (Human / AI)");
            }
            String playerType = App.scanner.next(playerPattern);

            switch (playerType.toLowerCase()) {
                case "human" -> players.add(new Human(brick));
                case "ai" -> players.add(new AI(brick));
                default -> throw new Exception("Failed to match input for player type: " + playerType);
            }
        }
    }

    public Brick getBrickAt(int column, int depth) {
        return (board.get(column).count > depth) ? board.get(column).brick : Brick.NONE;
    }

    public boolean hasBrickInTray(Brick brick) {
        return (brick == Brick.WHITE) ? winTrayWhite > 0 : winTrayBlack > 0;

    }

    public boolean isValidMove(Move move, Brick player, int roll) {
        // Check if the move is within bounds
        // Force reentry before other stones
        if (!move.isReentry() || !move.isBearingOff()) {
            if (move.to() > 23 || move.to() < 0) {
                return false;
            }
            if ((player == Brick.WHITE && barWhite > 0) || (player == Brick.BLACK && barBlack > 0)) {
                if (!move.isReentry()) {
                    return false;
                }
            }

            // Ensure a maximum of 5 stones in one location
            if (board.get(move.to()).count >= 5) {
                return false;
            }

            // Prevent moving a stone to a location where the opposite color has 2 or more stones
            BoardElement toPoint = board.get(move.to());
            if (toPoint.brick != Brick.NONE && toPoint.brick != player && toPoint.count > 1) {
                return false;
            }

            // Additional check for bearing off
            if (move.isBearingOff()) {
                return (player != Brick.WHITE || whiteHomeBoard == maxWhiteHomeBoard) &&
                        (player != Brick.BLACK || blackHomeBoard == maxBlackHomeBoard);
            }

            if(player == Brick.WHITE && barWhite == 0){
                return move.to() == move.from() + roll;

            }

            if(player == Brick.BLACK && barBlack == 0){
                return move.to() == move.from() - roll;

            }
        }
        return true;
    }
    // Note, does not check
        // validity!
    public void performMove(Move move) {
        if (move.isBearingOff()) {
            board.get(move.from()).count--;
            if (board.get(move.from()).count == 0) {
                board.get(move.from()).brick = Brick.NONE;
            }
            if (board.get(move.from()).brick == Brick.WHITE) {
                winTrayWhite++;
                maxWhiteHomeBoard--;
            } else if (board.get(move.from()).brick == Brick.BLACK) {
                winTrayBlack++;
                maxBlackHomeBoard--;
            }
        }
        else if (move.isReentry()) {
            if (board.get(move.from()).brick == Brick.WHITE) {
                barWhite--;
                board.get(move.to()).count++;
                board.get(move.to()).brick = Brick.WHITE;
            } else if (board.get(move.from()).brick == Brick.BLACK) {
                barBlack--;
                board.get(move.to()).count++;
                board.get(move.to()).brick = Brick.BLACK;
            }
        }

        else {
            if (board.get(move.from()).brick == Brick.WHITE && board.get(move.to()).count < 6){
                whiteHomeBoard++;
            }
            else if (board.get(move.from()).brick == Brick.BLACK && board.get(move.to()).count > 17){
                blackHomeBoard++;
            }
            else if (board.get(move.from()).brick == Brick.WHITE && board.get(move.from()).brick == Brick.BLACK && board.get(move.to()).count ==1){
                barBlack++;
                board.get(move.to()).count--;
                board.get(move.to()).brick = Brick.NONE;
            }
            else if (board.get(move.from()).brick == Brick.BLACK && board.get(move.from()).brick == Brick.WHITE && board.get(move.to()).count ==1){
                barWhite++;
                board.get(move.to()).count--;
                board.get(move.to()).brick = Brick.NONE;
            }
            board.get(move.from()).count--;
            board.get(move.to()).count++;
            board.get(move.to()).brick = board.get(move.from()).brick; // Set new brick to old brick
            if (board.get(move.from()).count == 0) {
                board.get(move.from()).brick = Brick.NONE;
            } // Set brick to none if board is empty
        }

    }

    @Override
    public Board clone() {
        // Returning a clone of the current object
        List<BoardElement> board = new ArrayList<>();
        for (BoardElement p : this.board) { board.add(p.clone()); }
        List<Player> players = new ArrayList<>();
        for (Player p : this.players) { players.add(p); } // NOTE: do not clone this since we can use players as duplicates
        return new Board(board, players);
    }
}
