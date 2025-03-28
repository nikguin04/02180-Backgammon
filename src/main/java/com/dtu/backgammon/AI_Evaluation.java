package com.dtu.backgammon;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.ai.AI;
import com.dtu.backgammon.ai.MonteCarlo;
import com.dtu.backgammon.player.Player;

//TODO: If you want to play MonteCarlo against Expectiminimax you have to change the writer in startGame()
public class AI_Evaluation {

    private static final int NUM_GAMES = 5; // Number of games to simulate
    private static final Random RANDOM = new Random();
    public static Writer resultLogWriter;

    public static void main(String[] args) throws IOException {
        // Initialize FileWriter here before starting the games
        Logger.init("log.txt");
        resultLogWriter = new FileWriter("game_results.csv");
        resultLogWriter.write("Game,Winner,Turns,Expectimax_Wins,MonteCarlo_Wins\n");

        int expectimaxWins = 0;
        int monteCarloWins = 0;

        // Run the games and log results
        for (int i = 1; i <= NUM_GAMES; i++) {
            Board board = new Board();
            Player[] players = setupAIPlayers(board); // Set up Expectimax vs. MonteCarlo

            board.startGame(); // Play the game
            Brick win_stone = board.returnWinner(); // Get winner
            int turns = board.getTurnCount(); // Get number of turns

            Player winner = null;
            if (win_stone == Brick.WHITE) {
                winner = players[0]; // Player 0 is the winner (Expectimax)
            } else if (win_stone == Brick.BLACK) {
                winner = players[1]; // Player 1 is the winner (Monte Carlo)
            }


            if (winner instanceof AI) {
                expectimaxWins++;
                resultLogWriter.write(i + ",Expectimax," + turns + "," + expectimaxWins + "," + monteCarloWins + "\n");
            } else if (winner instanceof MonteCarlo) {
                monteCarloWins++;
                resultLogWriter.write(i + ",MonteCarlo," + turns + "," + expectimaxWins + "," + monteCarloWins + "\n");
            } else {
                resultLogWriter.write(i + ",Draw," + turns + "," + expectimaxWins + "," + monteCarloWins + "\n");
            }

            if (i % 100 == 0) {
                System.out.println("Completed " + i + " games...");
            }
        }

        // Make sure to close the writers after the loop finishes
        resultLogWriter.close();
        Logger.close();
        System.out.println("Simulation complete. Results saved in game_results.csv.");
    }

    private static Player[] setupAIPlayers(Board board) {
        Player ai1, ai2;
        if (RANDOM.nextBoolean()) {
            ai1 = new AI(Brick.WHITE);
            ai2 = new MonteCarlo(Brick.BLACK);
        } else {
            ai1 = new MonteCarlo(Brick.WHITE);
            ai2 = new AI(Brick.BLACK);
        }

        board.players.add(ai1);
        board.players.add(ai2);
        return new Player[]{ai1, ai2};
    }

}