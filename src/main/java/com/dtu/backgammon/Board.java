package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.dtu.backgammon.player.Player;

public class Board {
    public enum Brick {
        NONE, WHITE, BLACK;

        public Brick opponent() {
            return this == WHITE ? BLACK : WHITE;
        }
    }

    public static class BoardElement {
        public Brick brick;
        public int count;
        public BoardElement(Brick brick, int count) { this.brick = brick;this.count = count; }
        @Override public BoardElement clone() { return new BoardElement(brick, count); }

        public Brick getBrick() {
            return brick;
        }

        public int getCount() {
            return count;
        }
    }

    int winTrayWhite = 0;
    int winTrayBlack = 0;

    int barWhite = 0;
    int barBlack = 0;

    int homeBoardWhite = 0;
    int homeBoardBlack = 0;

    int maxHomeBoardWhite = 15;
    int maxHomeBoardBlack = 15;

    public List<BoardElement> board;
    public List<Player> players = new ArrayList<>();

    public static int WHITE_START = -1;
    public static int BLACK_START = 24;
    public static int WHITE_DIR = 1;
    public static int BLACK_DIR = -1;

    public Board() {
        board = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            board.add(i, new BoardElement(Brick.NONE, 0));
        }

        //setupStandardBoard();
        setupDebugBoard();
    }

    public Board(List<BoardElement> board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public int getBarCount(Brick brick) {
        return (brick == Brick.WHITE) ? barWhite : barBlack;
    }

    public int getHomeBoardCount(Brick brick) {
        return (brick == Brick.WHITE) ? homeBoardWhite : homeBoardBlack;
    }

    public int getWinTrayCount(Brick brick) {
        return (brick == Brick.WHITE) ? winTrayWhite : winTrayBlack;
    }

    public BoardElement[] getPoints() {
        return board.toArray(new BoardElement[0]);
    }

    private void setupDebugBoard() {
        homeBoardWhite = 2;
        homeBoardBlack = 1;
        winTrayWhite = 13;
        winTrayBlack = 14;
        maxHomeBoardWhite = 2;
        maxHomeBoardBlack = 1;
        setColumn(23, Brick.WHITE, 2);
        setColumn(0, Brick.BLACK, 1);
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

        // Count initial checkers in home board
        for (int i = 0; i <= 5; i++) { // Black home board
            if (board.get(i).brick == Brick.BLACK) {
                homeBoardBlack += board.get(i).count;
            }
        }
        for (int i = 18; i <= 23; i++) { // White home board
            if (board.get(i).brick == Brick.WHITE) {
                homeBoardWhite += board.get(i).count;
            }
        }
    }

    public void setColumn(int column, Brick player, int count) {
        board.get(column).brick = player;
        board.get(column).count = count;
    }

    public void startGame() {
        outer:
        while (true) { // Outer loop will run until the game is over
            players:
            for (Player p : players) {
                Renderer.render(this);
                Dice d = new Dice();
                int[] roll = d.rollDice();
                List<Integer> rollList = Arrays.stream(roll).boxed().collect(Collectors.toList());
                List<Move> moveList = new ArrayList<>();

                while (!rollList.isEmpty()) {
                    if (this.clone().actions(rollList, p.brick).isEmpty()) { continue players; }

                    Move move = p.getMove(this, rollList);
                    if (!rollList.contains(move.getRoll())) {
                        continue;
                    }
                    boolean valid = this.isValidMove(move, p.brick, move.getRoll());
                    if (valid) {
                        moveList.add(move);
                        rollList.remove(Integer.valueOf(move.getRoll()));
                        this.performMove(move);
                        Renderer.render(this); // Always render after a move :)
                    }

                    if (isGameOver()) { break outer; }
                }
            }
        }
    }

    public Brick getBrickAt(int column, int depth) {
        return (board.get(column).count > depth) ? board.get(column).brick : Brick.NONE;
    }

    public boolean hasBrickInBar(Brick brick) {
        return (brick == Brick.WHITE) ? barWhite > 0 : barBlack > 0;
    }

    public boolean isValidMove(Move move, Brick player, int roll) {
        // Additional check for bearing off
        if (move.isBearingOff()) {
            return player == Brick.WHITE ? homeBoardWhite >= maxHomeBoardWhite : homeBoardBlack >= maxHomeBoardBlack;
        }
        // Validations rules for normal moves
        else {
            // Check if movement is within bounds
            if (move.to() > 23 || move.to() < 0) {
                return false;
            }
            // Check if the player has a stone on the bar
            if (!move.isReentry()) {
                if ((player == Brick.WHITE && barWhite > 0) || (player == Brick.BLACK && barBlack > 0)) {
                    return false;
                }
            }
            BoardElement toPoint = board.get(move.to());
            // Ensure a maximum of 5 stones in one location
            if (toPoint.count >= 5) {
                return false;
            }
            // Prevent moving a stone to a location where the opposite color has 2 or more stones
            if (toPoint.brick != Brick.NONE && toPoint.brick != player && toPoint.count > 1) {
                return false;
            }
            // Check if move is valid
            if (player == Brick.WHITE && barWhite == 0) {
                return move.to() == move.from() + roll;
            }
            // Check if move is valid
            if (player == Brick.BLACK && barBlack == 0) {
                return move.to() == move.from() - roll;
            }
        }
        return true;
    }

    public void performMoves(Move[] moves) {
        for (Move move: moves) {
            performMove(move);
        }
    }

    // Note, does not check validity!
    public void performMove(Move move) {
        if (move.isBearingOff()) {
            BoardElement fromPoint = board.get(move.from());
            if (fromPoint.count == 0) {
                fromPoint.brick = Brick.NONE;
            }
            if (fromPoint.brick == Brick.WHITE) {
                winTrayWhite++;
                maxHomeBoardWhite--;
            } else if (fromPoint.brick == Brick.BLACK) {
                winTrayBlack++;
                maxHomeBoardBlack--;
            }
            fromPoint.count--;
        } else if (move.isReentry()) {
            BoardElement toPoint = board.get(move.to());
            if (move.brick() == Brick.WHITE) {
                barWhite--;
                toPoint.count++;
                toPoint.brick = Brick.WHITE;
            } else if (move.brick() == Brick.BLACK) {
                barBlack--;
                toPoint.count++;
                toPoint.brick = Brick.BLACK;
            }
        } else {
            BoardElement fromPoint = board.get(move.from());
            BoardElement toPoint = board.get(move.to());
            if (fromPoint.brick == Brick.BLACK && move.to() <= 5 && move.from() > 5) { // TODO: Account for already in homeboard
                homeBoardBlack++;
            } else if (fromPoint.brick == Brick.WHITE && move.to() >= 18 && move.from() < 18) {
                homeBoardWhite++;
            }
            if (fromPoint.brick == Brick.WHITE && toPoint.brick == Brick.BLACK && toPoint.count == 1) {
                barBlack++;
                toPoint.count--;
                toPoint.brick = Brick.NONE;

                // If hit piece was in the black home board, decrease count
                if (move.to() <= 5) {
                    homeBoardBlack--;
                }
            } else if (fromPoint.brick == Brick.BLACK && toPoint.brick == Brick.WHITE && toPoint.count == 1) {
                barWhite++;
                toPoint.count--;
                toPoint.brick = Brick.NONE;

                // If hit piece was in the white home board, decrease count
                if (move.to() >= 18) {
                    homeBoardWhite--;
                }
            }
            fromPoint.count--;
            toPoint.count++;
            toPoint.brick = fromPoint.brick; // Set new brick to old brick
            if (fromPoint.count == 0) {
                // Set brick to none if board is empty
                fromPoint.brick = Brick.NONE;
            }
        }
    }

    @Override
    public Board clone() {
        // Returning a clone of the current object

        List<BoardElement> board = new ArrayList<>();
        for (BoardElement p : this.board) { board.add(p.clone()); }
        // NOTE: do not clone this since we can use players as duplicates
        List<Player> players = new ArrayList<>(this.players);
        Board newBoard = new Board(board, players);
        newBoard.winTrayWhite = winTrayWhite;
        newBoard.winTrayBlack = winTrayBlack;
        newBoard.barWhite = barWhite;
        newBoard.barBlack = barBlack;
        newBoard.homeBoardWhite = homeBoardWhite;
        newBoard.homeBoardBlack = homeBoardBlack;
        newBoard.maxHomeBoardWhite = maxHomeBoardWhite;
        newBoard.maxHomeBoardBlack = maxHomeBoardBlack;
        return newBoard;
    }

    public boolean isGameOver() {
        if (winTrayWhite == 15) {
            System.out.println("Congratulations White, you won the Game!!!");
            return true;
        } else if (winTrayBlack == 15) {
            System.out.println("Congratulations Black, you won the Game!!!");
            return true;
        }
        return false;
    }

    public List<Move[]> actions(List<Integer> rolls, Brick player) {
        List<BoardElement> boardElements = this.board;
        List<List<Integer>> diceMovesAnyOrder = new ArrayList<>();
        if (rolls.size() < 2 || (int) rolls.get(0) == rolls.get(1)) { // All eyes are equal
            diceMovesAnyOrder.add(rolls);
        } else {
            // Scuffed way of reversing dice moves
            diceMovesAnyOrder.add(List.of(rolls.get(0), rolls.get(1)));
            diceMovesAnyOrder.add(List.of(rolls.get(1), rolls.get(0)));
        }

        List<Move[]> moves = new ArrayList<>();
        for (List<Integer> diceMove : diceMovesAnyOrder) { // TODO: Make sure to break if no bricks but not all moves are used
            for (int i = 0; i < boardElements.size(); i++) {
                Move move;
                if (hasBrickInBar(player)) { // Player has brick in the tray and needs to move them out first
                    int startPos = player == Brick.BLACK ? Board.BLACK_START : Board.WHITE_START;
                    int toPosition = startPos + diceMove.get(0) * (player == Brick.BLACK ? -1 : 1); // Calculate the absolut to position for reentry
                    move = new Move(startPos, toPosition, Move.MoveType.REENTRY, player);
                    i = boardElements.size(); // Make sure to break loop if we have a brick in tray
                } else {
                    if (boardElements.get(i).brick != player) { continue; }
                    // The current player has bricks to move here

                    int toPosition = i + diceMove.get(0) * (player == Brick.BLACK ? -1 : 1);
                    // Players can only move in their right direction which means this logic works for either player
                    Move.MoveType movetype = toPosition > 23 || toPosition < 0 ? Move.MoveType.BEARINGOFF : Move.MoveType.NORMAL;
                    move = new Move(i, toPosition, movetype, player); // Goes 0->23 for white and 23->0 for black
                }

                if (!isValidMove(move, player, diceMove.get(0))) { continue; } // Move is not valid

                List<Integer> newDiceMoves = new ArrayList<>(diceMove);
                newDiceMoves.remove(0);

                if (!newDiceMoves.isEmpty()) {
                    Board newBoard = this.clone();
                    newBoard.performMove(move);
                    List<Move[]> actions = newBoard.actions(newDiceMoves, player);
                    if (actions.isEmpty()) { // We are either done with the game or there are no actions, so return just this move
                        moves.add(new Move[] { move });
                    } else { // Add all possible actions to move list
                        for (Move[] action : actions) {
                            List<Move> nestMoves = new ArrayList<>(Arrays.asList(action));
                            nestMoves.add(0, move);
                            moves.add(nestMoves.toArray(Move[]::new)); // Add list of new moves at this state to the moves array
                        }
                    }
                } else {
                    moves.add(new Move[] { move });
                }
            }
        }
        return moves;
    }
}
