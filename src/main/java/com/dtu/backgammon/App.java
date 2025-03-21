package com.dtu.backgammon;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.ai.AI;
import com.dtu.backgammon.player.Human;

/**
 * Hello world!
 */
public class App {
    public static Scanner scanner;
    public static Writer logWriter;

    public static void main(String[] args) throws IOException {

        logWriter = new FileWriter("log.txt");

        scanner = new Scanner(System.in);
        Board board = new Board();
        setupPlayers(board);
        board.startGame();

        logWriter.close();

        /*board.homeBoardWhite = 15;
        board.setColumn(21, Brick.WHITE, 1);
        board.setColumn(22, Brick.WHITE, 1);*/

        /*board.barWhite = 1;
        board.setColumn(21, Brick.WHITE, 1);
        board.setColumn(9, Brick.WHITE, 3);

        AI ai = (AI) board.players.get(0);

        List<Move[]> actions = board.actions(List.of(2, 2, 2, 2), ai.brick);
        for (Move[] action : actions) {
            System.out.println(Arrays.toString(action));
        }*/
    }

    private static void setupStandardCheckerSetup(Board board) {
        // Set up the standard checker setup
        board.setColumn(0, Brick.WHITE, 2);
        board.setColumn(11, Brick.WHITE, 5);
        board.setColumn(16, Brick.WHITE, 3);
        board.setColumn(18, Brick.WHITE, 5);

        board.setColumn(23, Brick.BLACK, 2);
        board.setColumn(12, Brick.BLACK, 5);
        board.setColumn(7, Brick.BLACK, 3);
        board.setColumn(5, Brick.BLACK, 5);
    }

    static void setupPlayers(Board board) {
        // Initialize players
        Pattern playerPattern = Pattern.compile("(human|ai)", Pattern.CASE_INSENSITIVE);
        for (Brick brick : Brick.values()) {
            if (brick == Brick.NONE) { continue; } // Do not initialize a player as no brick

            System.out.println("Please choose a player for " + brick.name() + " (Human / AI):");
            while (!scanner.hasNext(playerPattern)) {
                scanner.next(); // Remove current input in scanner buffer
                System.out.println("Please choose either (Human / AI)");
            }
            String playerType = scanner.next(playerPattern);

            switch (playerType.toLowerCase()) {
                case "human" -> board.players.add(new Human(brick));
                case "ai" -> board.players.add(new AI(brick));
                default -> throw new IllegalStateException("Failed to match input for player type: " + playerType);
            }
        }
        scanner.nextLine(); // Flush scanner
    }
}
