package com.dtu.backgammon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        //setupDebugBoard();
        startGame();
    }
    public Board(List<BoardElement> board, List<Player> players,
        int winTrayWhite,
        int winTrayBlack,
        int barWhite,
        int barBlack,
        int blackHomeBoard,
        int whiteHomeBoard
        ) {
        this.board = board; this.players = players;
        this.winTrayWhite = winTrayWhite;
        this.winTrayBlack = winTrayBlack;
        this.barWhite = barWhite;
        this.barBlack = barBlack;
        this.blackHomeBoard = blackHomeBoard;
        this.whiteHomeBoard = whiteHomeBoard;
    }

    private void setupDebugBoard() {
        whiteHomeBoard = 15;
        blackHomeBoard = 15;
        setColumn(0, Brick.WHITE, 2);
        setColumn(1, Brick.WHITE, 2);
        setColumn(4, Brick.BLACK, 1);
        setColumn(8, Brick.BLACK, 1);
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
                blackHomeBoard += board.get(i).count;
            }
        }
        for (int i = 18; i <= 23; i++) { // White home board
            if (board.get(i).brick == Brick.WHITE) {
                whiteHomeBoard += board.get(i).count;
            }
        }


    }
    public void setColumn(int column, Brick player, int count) {
        board.get(column).brick = player;
        board.get(column).count = count;
    }
    
    private void startGame() {
        while (true) { // TODO: Replace this with a winning condition
            for (Player p : players) {
                Renderer.render(this);
                Dice d = new Dice();
                int[] roll = d.rollDice();
                List<Integer> rollList = Arrays.stream(roll).boxed().collect(Collectors.toList());
                List<Move> moveList = new ArrayList<>();
                while (rollList.size() > 0) {
                    Move move = p.getMove(this, rollList);
                    if (!rollList.contains(Integer.valueOf(move.getRoll()))) {
                        continue;
                    }
                    boolean valid = this.isValidMove(move, p.brick, move.getRoll());
                    if (valid) {
                        moveList.add(move);
                        rollList.remove(Integer.valueOf(move.getRoll()));
                        this.performMove(move);
                    }
                }

            }

        }
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
        App.scanner.nextLine(); // Flush scanner
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
            return (player != Brick.WHITE || whiteHomeBoard >= maxWhiteHomeBoard) &&
                    (player != Brick.BLACK || blackHomeBoard >= maxBlackHomeBoard);
        }
        //Validations rules for normal moves
        else   {
            //Check if movement is within bounds
            if (move.to() > 23 || move.to() < 0) {
                return false;
            }
            //Check if the player has a stone on the bar
            if(!move.isReentry()) {
                if ((player == Brick.WHITE && barWhite > 0) || (player == Brick.BLACK && barBlack > 0)) {
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
            // Check if move is valid
            if(player == Brick.WHITE && barWhite == 0){
                return move.to() == move.from() + roll;

            }
           // Check if move is valid
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
            if (move.brick() == Brick.WHITE) {
                barWhite--;
                board.get(move.to()).count++;
                board.get(move.to()).brick = Brick.WHITE;
            } else if (move.brick() == Brick.BLACK) {
                barBlack--;
                board.get(move.to()).count++;
                board.get(move.to()).brick = Brick.BLACK;
            }
        }

        else {
            if (board.get(move.from()).brick == Brick.BLACK && move.to() <= 5 && move.from() > 5){ // TODO: Account for already in homeboard
                blackHomeBoard++;
            }
            else if (board.get(move.from()).brick == Brick.WHITE && move.to() >= 18 && move.from() < 18){
                whiteHomeBoard++;
            }
            if (board.get(move.from()).brick == Brick.WHITE && board.get(move.to()).brick == Brick.BLACK && board.get(move.to()).count ==1){
                barBlack++;
                board.get(move.to()).count--;
                board.get(move.to()).brick = Brick.NONE;

                // If hit piece was in the black home board, decrease count
                if (move.to() <= 5 ) {
                    blackHomeBoard--;
                }

            }
            else if (board.get(move.from()).brick == Brick.BLACK && board.get(move.to()).brick == Brick.WHITE && board.get(move.to()).count ==1){
                barWhite++;
                board.get(move.to()).count--;
                board.get(move.to()).brick = Brick.NONE;

                // If hit piece was in the white home board, decrease count
                if (move.to() >= 18) {
                    whiteHomeBoard--;
                }

            }
            board.get(move.from()).count--;
            board.get(move.to()).count++;
            board.get(move.to()).brick = board.get(move.from()).brick; // Set new brick to old brick
            if (board.get(move.from()).count == 0) {
                board.get(move.from()).brick = Brick.NONE;
            } // Set brick to none if board is empty
        }

        Renderer.render(this); // Always render after a move :)
    }

    @Override
    public Board clone() {
        // Returning a clone of the current object

        List<BoardElement> board = new ArrayList<>();
        for (BoardElement p : this.board) { board.add(p.clone()); }
        List<Player> players = new ArrayList<>();
        for (Player p : this.players) { players.add(p); } // NOTE: do not clone this since we can use players as duplicates
        return new Board(board, players, winTrayWhite, winTrayBlack, barWhite, barBlack, blackHomeBoard, whiteHomeBoard);
    }
}
