package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
        public Brick brick;
        public int count;
        public BoardElement(Brick brick, int count) { this.brick = brick; this.count = count; }
        @Override
        public BoardElement clone() { return new BoardElement(brick, count); }
    }

    public List<BoardElement> board;
    public List<Player> players = new ArrayList<Player>();

    public static int WHITE_START = -1;
    public static int BLACK_START = 24;

    public Board() throws Exception {
        board = new ArrayList<BoardElement>(24);
        for (int i = 0; i < 24; i++) {
            board.add(i, new BoardElement(Brick.NONE, 0));
        }

        setupPlayers();
    }
    public Board(List<BoardElement> board, List<Player> players) {
        this.board = board; this.players = players;
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

    public boolean isValidMove(Move move, Brick player, int roll) {
        if (move.isBearingOff()) {
            // TODO: check that all stones are on home board
            return move.from() < roll;
        }
        // TODO: Force player to reenter stones from the bar
        BoardElement toPoint = board.get(move.to());
        if (toPoint.brick != Brick.NONE && toPoint.brick != player && toPoint.count > 1) {
            return false;
        }
        // TODO: Other edge cases like the one described in https://en.wikipedia.org/wiki/Backgammon#Bearing_off?
        return true;
    }

    // Note, does not check validity!
    public void performMove(Move move) {
        board.get(move.from()).count--;
        board.get(move.to()).count++;
        board.get(move.to()).brick = board.get(move.from()).brick; // Set new brick to old brick
        if (board.get(move.from()).count == 0) { board.get(move.from()).brick = Brick.NONE; } // Set brick to none if board is empty
        
    }
    


    @Override
    public Board clone() {
        // Returning a clone of the current object
        List<BoardElement> boarde = new ArrayList<>();
        for (BoardElement p : this.board) {boarde.add(p.clone());}
        List<Player> players = new ArrayList<>();
        for (Player p : this.players) {players.add(p);} // NOTE: do not clone this since we can use players as duplicates
        return new Board(boarde, players);
    }
}
