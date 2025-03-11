package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.dtu.backgammon.player.AI;
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
        Brick brick;
        int count;
        public BoardElement(Brick brick, int count) { this.brick = brick; this.count = count; }
        @Override
        public BoardElement clone() { return new BoardElement(brick, count); }
    }

    public List<BoardElement> board;
    public List<Player> players = new ArrayList<>();
    public Board() throws Exception {
        board = new ArrayList<>(24);
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
            if(player == Brick.WHITE && barWhite == 0){
                return move.to() == move.from() + roll;

            }

            if(player == Brick.BLACK && barBlack == 0){
                return move.to() == move.from() - roll;

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

    public List<Move[]> actions(Brick player, List<Integer> diceMoves) { // This returns an array of some amount of moves to perform. This means that the return in ALL possible moves
        List<List<Integer>> diceMovesAnyOrder = new ArrayList<>();
        if (diceMoves.size() < 2 || diceMoves.get(0) == diceMoves.get(1)) { // All eyes are equal
            diceMovesAnyOrder.add(diceMoves);
        } else {
            // Scuffed way of reversing dice moves
            diceMovesAnyOrder.add(new ArrayList<>( Arrays.asList(new Integer[] {diceMoves.get(0), diceMoves.get(1)}) ));
            diceMovesAnyOrder.add(new ArrayList<>( Arrays.asList(new Integer[] {diceMoves.get(1), diceMoves.get(0)}) ));
        }

        List<Move[]> moves = new ArrayList<Move[]>();
        for (List<Integer> diceMove : diceMovesAnyOrder) {
            for (int i = 0; i < board.size(); i++) {
                if (board.get(i).brick == player) { // The current player has bricks to move here
                    Move move = new Move(i, i + diceMove.get(0) * (player == Brick.BLACK ? -1 : 1), Move.MoveType.NORMAL); // Goes 0->23 for white and 23->0 for black

                    if (move.to() > 23 || move.to() < 0) { continue; } // TODO: Implement the logic for bearing off and reentry
                    if (!this.isValidMove(move, player, diceMove.get(0))) { continue; } // Move is not valid

                    List<Integer> newDiceMoves = new ArrayList<>(diceMove);
                    newDiceMoves.remove(0);

                    if (newDiceMoves.size() > 0) {
                        Board newBoard = this.clone();
                        newBoard.performMove(move);
                        List<Move[]> actions = newBoard.actions(player, newDiceMoves);
                        for (int j = 0; j < actions.size(); j++) {
                            List<Move> nestMoves = new ArrayList<>(Arrays.asList(actions.get(j)));
                            nestMoves.add(0, move);
                            moves.add( nestMoves.toArray(Move[]::new) ); // Add list of new moves at this state to the moves array
                        }
                    } else {
                        moves.add(new Move[] { move });
                    }
                }
            }
        }
        return moves;
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
