package com.dtu.backgammon.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Comparator;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.player.Player;

public class MonteCarlo extends Player {
    private static final int SIMULATION_COUNT = 100; // Number of simulations per move
    private static final int MAX_DEPTH = 2; // Max depth for simulations

    public MonteCarlo(Board.Brick brick) {
        super(brick);
    }

    public Move[] getMove(Board board, List<Integer> roll) {
        // Use MCTS to get the best sequence of moves
        return MonteCarlo.getBestMove(board, roll, brick);
    }

    public static Move[] getBestMove(Board board, List<Integer> roll, Brick brick) {
        System.out.println(roll);
        System.out.println(brick.name());
        Board boardClone = board.clone();
        List<Move[]> possibleMoves = boardClone.actions(roll, brick);

        // Simulate all possible move sequences
        List<Node> nodes = new ArrayList<>();
        for (Move[] moveSequence : possibleMoves) {
            Node node = new Node(moveSequence, boardClone.clone(), brick, null);
            nodes.add(node);
        }

        // Run simulations for each possible move sequence
        for (Node node : nodes) {
            for (int i = 0; i < SIMULATION_COUNT; i++) {
                runSimulation(node);
            }
        }

        // Select the move sequence with the highest win rate
        Node bestNode = nodes.stream()
            .max(Comparator.comparingDouble(MonteCarlo::ucbValue))
            .orElseThrow();

        return bestNode.moveSequence;
    }

    // Simulate a game from a given node
    private static void runSimulation(Node node) {
        Random random = new Random();
        Board boardCopy = node.board.clone();
        Brick currentPlayer = node.brick;
        List<Integer> roll;

        // Perform random moves until the game ends or the max depth is reached
        int depth = 0;
        while (!boardCopy.isGameOver() && depth < MAX_DEPTH) {
            roll = generateRandomRoll();
            List<Move[]> possibleMoves = boardCopy.actions(roll, currentPlayer);

            if (possibleMoves.isEmpty()) { return; }

            Move[] randomMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
            for (Move move : randomMove) {
                boardCopy.performMove(move);
            }

            currentPlayer = currentPlayer.opponent();
            depth++;
        }

        // Backpropagate the result of this simulation (win or loss)
        if (boardCopy.getWinner() == node.brick) {
            node.wins++;
        } else {
            node.losses++;
        }
    }

    // Generate a random dice roll for the simulation
    private static List<Integer> generateRandomRoll() {
        Random rand = new Random();
        int die1 = rand.nextInt(6) + 1;
        int die2 = rand.nextInt(6) + 1;
        return Arrays.asList(die1, die2);
    }

    private static double ucbValue(Node node) {
        int wins = node.wins;
        int visits = node.visits; // The number of times this move has been explored

        int totalVisits = node.getTotalVisits();

        // The constant C controls the exploration-exploitation tradeoff
        double C = 1.41; // 1.41 is a standard value for UCB

        // UCB formula
        double averageReward = (double) wins / visits;
        double explorationFactor = C * Math.sqrt(Math.log(totalVisits) / visits);

        return averageReward + explorationFactor;
    }

    public String getName() {
        return "MonteCarloAI";
    }
}
