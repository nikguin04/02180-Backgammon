package com.dtu.backgammon;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.ai.AI;
import java.io.FileWriter;

public class App {
    public static Scanner scanner;
    public static FileWriter writer;

    public static void main(String[] args) throws IOException {
        // Set up the CSV file for writing results

        writer = new FileWriter("log.txt");


            int totalGames = 10;

            // Loop to simulate 100 games
            for (int i = 1; i <= totalGames; i++) {
                Board board = new Board();
                setupPlayers(board);
                board.startGame();

                System.out.println("Game " + i + " complete.\n");
                writer.close();
            }

        System.out.println("100 games complete. Results logged to 'evaluation_results.csv'.");
    }

    private static void setupPlayers(Board board) {
        // Initialize AI players for both sides (both players are AI)
        board.players.clear(); // Clear previous players setup

        System.out.println("AI vs AI setup");

        // Setup AI for both WHITE and BLACK
        board.players.add(new AI(Brick.WHITE));
        board.players.add(new AI(Brick.BLACK));
    }




}
