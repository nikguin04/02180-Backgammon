package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.List;

import com.dtu.backgammon.player.Player;

public class Board implements Cloneable {
    public enum Brick {
        NONE, WHITE, BLACK;

        public Brick opponent() {
            return this == WHITE ? BLACK : WHITE;
        }
    }

    public record Point(Brick brick, int count) {
        public static Point EMPTY = new Point(Brick.NONE, 0);
        public Point withOneLess() {
            assert count >= 1;
            return new Point(count > 1 ? brick : Brick.NONE, count - 1);
        }
        public Point withOneMore(Brick brick) {
            assert count >= 0;
            return new Point(brick, count + 1);
        }
    }

    int turns;

    int winTrayWhite = 0;
    int winTrayBlack = 0;

    int barWhite = 0;
    int barBlack = 0;

    int homeBoardWhite = 0;
    int homeBoardBlack = 0;

    int maxHomeBoardWhite = 15;
    int maxHomeBoardBlack = 15;

    public Point[] board;
    public List<Player> players = new ArrayList<>();

    public static int WHITE_START = -1;
    public static int BLACK_START = 24;
    public static int WHITE_DIR = 1;
    public static int BLACK_DIR = -1;

    public Board() {
        board = new Point[24];
        for (int i = 0; i < 24; i++) {
            board[i] = new Point(Brick.NONE, 0);
        }

        setupStandardBoard();
        //setupDebugBoard();
    }

    public Board(Point[] board, List<Player> players) {
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
            if (board[i].brick == Brick.BLACK) {
                homeBoardBlack += board[i].count;
            }
        }
        for (int i = 18; i <= 23; i++) { // White home board
            if (board[i].brick == Brick.WHITE) {
                homeBoardWhite += board[i].count;
            }
        }
    }

    public void setColumn(int column, Brick player, int count) {
        board[column] = new Point(player, count);
    }

    public void startGame() {
        int turnCount = 0; // To track the number of turns
        Dice dice = new Dice();
        outer:
        while (true) { // Outer loop will run until the game is over
            turns++;
            players:
            for (Player p : players) {
                Renderer.render(this);
                List<Integer> roll = dice.rollDice();
                p.totalMoveValue += dice.getTotalMoveValue();

                rolls:
                while (!roll.isEmpty()) {
                    if (this.clone().actions(roll, p.brick).isEmpty()) {
                        System.out.println("You can't do anything, skipping turn");
                        Logger.write("Skipped " + p.brick.name() + "'s turn\n");
                        //try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                        continue players;
                    }

                    Move[] moves = p.getMove(this, roll);
                    for (Move move : moves) {
                        if (!roll.contains(move.getRoll())) { continue rolls; }
                        if (!this.isValidMove(move, p.brick, move.getRoll())) { continue rolls; }

                        roll.remove(Integer.valueOf(move.getRoll()));
                        Logger.write(move + "\n");
                        this.performMove(move);
                        Renderer.render(this); // Always render after a move :)

                        if (isGameOver()) { break outer; }
                    }
                }
            }
        }

        // Game is over, print the winner
        System.out.println("Congratulations " + getWinner() + ", you won the Game!!!");

        Logger.write(players.get(0).brick.name() + " total move values:" + players.get(0).totalMoveValue + "\n");
        Logger.write(players.get(1).brick.name() + " total move values:" + players.get(1).totalMoveValue + "\n");
    }

    public Brick getBrickAt(int column, int depth) {
        return (board[column].count > depth) ? board[column].brick : Brick.NONE;
    }

    public boolean hasBrickInBar(Brick brick) {
        return (brick == Brick.WHITE) ? barWhite > 0 : barBlack > 0;
    }

    public boolean isValidMove(Move move, Brick player, int roll) {
        if (!move.isReentry() && board[move.from()].brick != player) {
            return false;
        }
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
            Point toPoint = board[move.to()];
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
            Point fromPoint = board[move.from()];
            if (fromPoint.brick == Brick.WHITE) {
                winTrayWhite++;
                homeBoardWhite--;
                maxHomeBoardWhite--;
            } else if (fromPoint.brick == Brick.BLACK) {
                winTrayBlack++;
                homeBoardBlack--;
                maxHomeBoardBlack--;
            }
            board[move.from()] = fromPoint.withOneLess();
        } else {
            Point toPoint = board[move.to()];
            if (move.brick() == Brick.BLACK && move.to() <= 5 && move.from() > 5) {
                homeBoardBlack++;
            } else if (move.brick() == Brick.WHITE && move.to() >= 18 && move.from() < 18) {
                homeBoardWhite++;
            }
            if (move.brick() == Brick.WHITE && toPoint.brick == Brick.BLACK && toPoint.count == 1) {
                barBlack++;
                toPoint = board[move.to()] = Point.EMPTY;

                // If hit piece was in the black home board, decrease count
                if (move.to() <= 5) {
                    homeBoardBlack--;
                }
            } else if (move.brick() == Brick.BLACK && toPoint.brick == Brick.WHITE && toPoint.count == 1) {
                barWhite++;
                toPoint = board[move.to()] = Point.EMPTY;

                // If hit piece was in the white home board, decrease count
                if (move.to() >= 18) {
                    homeBoardWhite--;
                }
            }
            board[move.to()] = toPoint.withOneMore(move.brick());
            if (move.isReentry()) {
                if (move.brick() == Brick.WHITE) {
                    barWhite--;
                } else if (move.brick() == Brick.BLACK) {
                    barBlack--;
                }
            } else {
                board[move.from()] = board[move.from()].withOneLess();
            }
        }
    }

    public int getTotalBrickProgress(Brick brick) {
        int total = 0;
        for (int i = 0; i < board.length; i++) {
            Point be = board[i];
            int distToHome = brick == Brick.WHITE ? i : 23-i;
            if (be.brick == brick) { total += be.count * distToHome; }
        }
        return total;
    }

    public boolean isGameOver() {
        return winTrayWhite == 15 || winTrayBlack == 15;
    }

    public Brick getWinner() {
        if (!isGameOver()) { return null; }
        return winTrayWhite == 15 ? Brick.WHITE : Brick.BLACK;
    }

    public List<Move[]> actions(List<Integer> rolls, Brick player) {
        List<List<Integer>> diceMovesAnyOrder;
        if (rolls.size() < 2 || (int) rolls.get(0) == rolls.get(1)) { // All eyes are equal
            diceMovesAnyOrder = List.of(rolls);
        } else {
            // Scuffed way of reversing dice moves
            diceMovesAnyOrder = List.of(List.of(rolls.get(0), rolls.get(1)), List.of(rolls.get(1), rolls.get(0)));
        }

        List<Move[]> moves = new ArrayList<>();
        for (List<Integer> diceMove : diceMovesAnyOrder) { // TODO: Make sure to break if no bricks but not all moves are used
            List<Integer> newDiceMoves = diceMove.subList(1, diceMove.size());
            for (int i = 0; i < board.length; i++) {
                Move move;
                if (hasBrickInBar(player)) { // Player has brick in the tray and needs to move them out first
                    int startPos = player == Brick.BLACK ? Board.BLACK_START : Board.WHITE_START;
                    int toPosition = startPos + diceMove.get(0) * (player == Brick.BLACK ? -1 : 1); // Calculate the absolut to position for reentry
                    move = new Move(startPos, toPosition, Move.MoveType.REENTRY, player);
                    i = board.length; // Make sure to break loop if we have a brick in tray
                } else {
                    if (board[i].brick != player) { continue; }
                    // The current player has bricks to move here

                    int toPosition = i + diceMove.get(0) * (player == Brick.BLACK ? -1 : 1);
                    // Players can only move in their right direction which means this logic works for either player
                    Move.MoveType movetype = toPosition > 23 || toPosition < 0 ? Move.MoveType.BEARINGOFF : Move.MoveType.NORMAL;
                    move = new Move(i, toPosition, movetype, player); // Goes 0->23 for white and 23->0 for black
                }

                if (!isValidMove(move, player, diceMove.get(0))) { continue; } // Move is not valid

                if (!newDiceMoves.isEmpty()) {
                    Board newBoard = this.clone();
                    newBoard.performMove(move);
                    List<Move[]> actions = newBoard.actions(newDiceMoves, player);
                    if (actions.isEmpty()) { // We are either done with the game or there are no actions, so return just this move
                        moves.add(new Move[] { move });
                    } else { // Add all possible actions to move list
                        for (Move[] action : actions) {
                            // Add list of new moves at this state to the moves array
                            Move[] nestMoves = new Move[action.length + 1];
                            nestMoves[0] = move;
                            System.arraycopy(action, 0, nestMoves, 1, action.length);
                            moves.add(nestMoves);
                        }
                    }
                } else {
                    moves.add(new Move[] { move });
                }
            }
        }
        return moves;
    }

    public List<Integer> getBlots(Brick brick) {
        List<Integer> blotCols = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            Point p = board[i];
            if (p.brick == brick && p.count == 1) {
                blotCols.add(i);
            }
        }
        return blotCols;
    }

    @Override
    public Board clone() {
        try {
            Board newBoard = (Board) super.clone();
            newBoard.board = board.clone();
            return newBoard;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
