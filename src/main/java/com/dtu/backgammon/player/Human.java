package com.dtu.backgammon.player;

import java.util.Scanner;
import java.util.regex.Pattern;

import com.dtu.backgammon.App;
import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;

public class Human extends Player {

    public Human(Brick brick, Board board) {
        super(brick, board);
    }

    @Override
    public void getMove() {
        Scanner scanner = App.scanner;
        String moveInput;

        while (true) {
            System.out.println("Enter your move (e.g., 'from to'): ");
            moveInput = scanner.nextLine();

            // Validate the input format
            if (Pattern.matches("\\d+ \\d+", moveInput)) {
                // Split the input and parse the positions
                String[] positions = moveInput.split(" ");
                int from = Integer.parseInt(positions[0]);
                int to = Integer.parseInt(positions[1]);

                // Validate the positions
                if (from >= 0 && from < 24 && to >= 0 && to < 24) {
                    // Create and store the move
                    Move move = new Move(from, to);
                    System.out.println(move);
                    break;
                } else {
                    System.out.println("Invalid move. Positions must be between 0 and 23.");
                }
            } else {
                System.out.println("Invalid input format. Please enter two numbers separated by a space.");
            }
        }
    }

    @Override
    public String getName() {
        return "Human";
    }
}
