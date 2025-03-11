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
            } else if (Pattern.matches("\\d+ W \\d+", moveInput)) {
                String[] positions = moveInput.split(" ");
                int from = Integer.parseInt(positions[0]);
                int windice = Integer.parseInt(positions[2]);
                int weighed_to = brick == Brick.WHITE ? from+windice : from-windice; // Weighed to so dice numbers actually make sense in validation
                if (from >= 0 && from < 24 && (weighed_to > 23 || weighed_to < 0) ) { 
                    return new Move(from, weighed_to, Move.MoveType.BEARINGOFF, brick); // To is either 24 or -1, so we can calculate the roll (from -> to) properly from both side
                }

            } else if(Pattern.matches("B \\d+", moveInput)) {
                String[] positions = moveInput.split(" ");
                int to = Integer.parseInt(positions[1]);
                if (to >= 0 && to < 24) { 
                    return new Move(brick == Brick.WHITE ? -1 : 24, to, Move.MoveType.REENTRY, brick); // From is either -1 or 24, so we can calculate the roll (from -> to) properly from both side
                    
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
