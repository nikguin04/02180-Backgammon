package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.Arrays;
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

    public boolean isValidMove(Move move, Brick player, int roll) {
        if (move.isBearingOff()) {
            // TODO: check that all stones are on home board
            return move.from() < roll;
        }
        // TODO: Force player to reenter stones from the bar
        BoardElement toPoint = board.get(move.from());
        if (toPoint.brick != Brick.NONE && toPoint.brick != player && toPoint.count > 1) {
            return false;
        }
        // TODO: Other edge cases like the one described in https://en.wikipedia.org/wiki/Backgammon#Bearing_off?
        return true;
    }
    
    public Move[][] actions(Brick player, List<Integer> diceMoves) { // This returns an array of some amount of moves to perform. This means that the return in ALL possible moves 
        List<Move[]> moves = new ArrayList<Move[]>();
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).brick == player) { // The current player has bricks to move here
                Move move = new Move(i, i-diceMoves.get(0)); // This should be able to go in either direction
                // TODO: Check that move is not below 0 and above 23
                if (!this.isValidMove(move, player, diceMoves.get(0))) { continue; } // Move is not valid

                diceMoves.remove(0);
                
                if (diceMoves.size() > 0) {
                    Board newBoard = this.clone();
                    newBoard.performMove(move);
                    Move[][] actions = newBoard.actions(player, diceMoves);
                    for (int j = 0; j < actions.length; j++) {
                        List<Move> nestMoves = Arrays.asList(actions[j]);
                        nestMoves.add(0, move);
                        moves.add( (Move[]) nestMoves.toArray() );
                    }
                } else {
                    moves.add(new Move[] { move });
                }
            }
        }
        return (Move[][])moves.toArray();
    }


    @Override
    public Board clone() {
        // Returning a clone of the current object
        try {
            return (Board) super.clone(); 
        } catch (CloneNotSupportedException e) {
            System.err.println("Error with cloning board, exiting.");
            System.exit(1);
            return this;
        }
    }
}
