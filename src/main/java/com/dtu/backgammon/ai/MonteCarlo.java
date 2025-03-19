package com.dtu.backgammon.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Comparator;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;

public class MonteCarlo {
    private static final int SIMULATION_COUNT = 1000;  // Number of simulations per move
    private static final int MAX_DEPTH = 4;  // Max depth for simulations

    public static Move[] getBestMove(Board board, List<Integer> roll, Brick brick) {
        System.out.println(roll);
        System.out.println(brick.name());
        Board boardClone = board.clone();
        List<Move[]> possibleMoves = boardClone.actions(roll, brick);


        // Simulate all possible move sequences
        List<Node> nodes = new ArrayList<>();
        for (Move[] moveSequence : possibleMoves) {
            Node node = new Node(moveSequence, boardClone.clone(), brick);
            nodes.add(node);
        }

        // Run simulations for each possible move sequence
        for (Node node : nodes) {
            for (int i = 0; i < SIMULATION_COUNT; i++) {
                runSimulation(node);
            }
        }

        // Select the move sequence with the highest win rate
        Node bestNode = nodes.stream().max(Comparator.comparingInt(Node::getWins)).orElseThrow();
        return bestNode.getMoveSequence();
    }

    // Simulate a game from a given node
    private static void runSimulation(Node node) {
        Board boardCopy = node.getBoard().clone();
        Brick currentPlayer = node.getBrick();
        List<Integer> roll = generateRandomRoll();




        // Perform random moves until the game ends or the max depth is reached
        int depth = 0;
        while (!boardCopy.isGameOver() && depth < MAX_DEPTH) {
            List<Move[]> possibleMoves = boardCopy.actions(roll, currentPlayer);

            if (possibleMoves.isEmpty()){return;}



            Move[] randomMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
            for (Move move : randomMove) {
                boardCopy.performMove(move);
            }

            currentPlayer = currentPlayer.opponent();
            roll = generateRandomRoll();
            depth++;
        }

        // Backpropagate the result of this simulation (win or loss)
        if (boardCopy.getWinner() == node.getBrick()) {
            node.incrementWins();
        } else {
            node.incrementLosses();
        }
    }

    // Generate a random dice roll for the simulation
    private static List<Integer> generateRandomRoll() {
        Random rand = new Random();
        int die1 = rand.nextInt(6) + 1;
        int die2 = rand.nextInt(6) + 1;
        return Arrays.asList(die1, die2);
    }

}
