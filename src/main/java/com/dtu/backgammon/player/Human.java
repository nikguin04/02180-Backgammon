package com.dtu.backgammon.player;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.dtu.backgammon.App;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;

public class Human extends Player {

    public Human(Brick brick) {
        super(brick);
    }

    @Override
    public Move getMove(List<Integer> roll) {
        Scanner scanner = App.scanner;
        String moveInput;

        while (true) {
            System.out.println(brick.name() + " To move with " + roll.toString());
            System.out.println("Enter your move (e.g., 'from to'): ");
            moveInput = scanner.nextLine();

            // Validate the input format
            if (Pattern.matches("\\d+ \\d+", moveInput)) {
                // Split the input and parse the positions
                String[] positions = moveInput.split(" ");
                int from = Integer.parseInt(positions[0]);
                int to = Integer.parseInt(positions[1]);

                // TODO: Implement input for bering off and reentry
                // Validate the positions
                if (from >= 0 && from < 24 && to >= 0 && to < 24) { 
                    // Create and store the move
                    Move move = new Move(from, to, Move.MoveType.NORMAL, brick);
                    return move;
                }
            } else if (Pattern.matches("\\d+ W", moveInput)) {
                String[] positions = moveInput.split(" ");
                int from = Integer.parseInt(positions[0]);
                if (from >= 0 && from < 24) { 
                    return new Move(from, -1, Move.MoveType.BEARINGOFF, brick);
                }

            } else if(Pattern.matches("B \\d+", moveInput)) {
                String[] positions = moveInput.split(" ");
                int to = Integer.parseInt(positions[1]);
                if (to >= 0 && to < 24) { 
                    return new Move(-1, to, Move.MoveType.REENTRY, brick);
                    
                }
            } else {
                System.out.println("Invalid input format. Please enter two numbers separated by a space.");
            }
            System.out.println("Invalid move. Positions must be between 0 and 23, Bearing (W), or Barring out (B)");
        }
    }

    @Override
    public String getName() {
        return "Human";
    }
}
