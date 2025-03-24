package com.dtu.backgammon;

import java.io.IOException;
import java.util.Scanner;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.ai.AI;
import com.dtu.backgammon.ai.MonteCarlo;
import com.dtu.backgammon.player.Human;
import com.dtu.backgammon.player.Player;

/**
 * Hello world!
 */
public class App {
    public static Scanner scanner;
    public static final boolean enableEvalWriter = false;

    public static void main(String[] args) throws IOException {

        Logger.init("log.txt");
        if (enableEvalWriter) {
            Logger.initEval("eval.csv");
        }

        scanner = new Scanner(System.in);
        Board board = new Board();
        setupPlayers(board);
        Renderer.clearScreen();
        board.startGame();

        Logger.close();

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
        for (Brick brick : Brick.values()) {
            if (brick == Brick.NONE) { continue; } // Do not initialize a player as no brick

            System.out.println("Please choose a player for " + brick.name() + " (Human / Expectiminimax / MonteCarlo):");
            Player player = null;
            while (player == null) {
                String playerType = scanner.nextLine().toLowerCase();
                if ("human".startsWith(playerType)) {
                    player = new Human(brick);
                } else if ("expectiminimax".startsWith(playerType)) {
                    player = new AI(brick);
                } else if ("montecarlo".startsWith(playerType)) {
                    player = new MonteCarlo(brick);
                } else {
                    System.out.println("Please choose either (Human / Expectiminimax / MonteCarlo)");
                }
            }
            board.players.add(player);
        }
    }
}
