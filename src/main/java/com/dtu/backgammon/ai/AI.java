package com.dtu.backgammon.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.player.Player;

public class AI extends Player {
    private static final int MAX_DEPTH = 2;
    public static final Roll[] ALL_ROLLS;
    public static final int NUM_ROLLS = 6 * 6;

    private static final ForkJoinPool pool = new ForkJoinPool(); // Global thread pool

    public AI(Brick brick) {
        super(brick);
    }

    @Override
    // Figure out the best first move, and then find the highest eval move
    public Move[] getMove(Board board, List<Integer> roll) {
        Board boardClone = board.clone();
        Move[] bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        List<Move[]> possibleMoves = boardClone.actions(roll, brick);

        // For each possible set of moves, clone the board and perform the move sequence
        List<ExpectiminimaxTask> tasks = new ArrayList<>();

        // Parallelize expectiminimax at depth 1
        for (Move[] moveSequence : possibleMoves) {

            Board simulatedBoard = boardClone.clone();
            for (Move move : moveSequence) {
                simulatedBoard.performMove(move);
            }

            // This function acts as depth 0, so start expectiminimax at depth 1
            ExpectiminimaxTask task = new ExpectiminimaxTask(simulatedBoard, 1, false, brick, Integer.MIN_VALUE, Integer.MAX_VALUE);
            tasks.add(task);
            task.fork(); // Start in parallel
        }

        // Collect results and find the best move
        for (int i = 0; i < tasks.size(); i++) {
            int moveValue = tasks.get(i).join(); // Wait for computation
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = possibleMoves.get(i);
            }
        }

        if (bestMove == null) {
            throw new IllegalStateException("No valid move found!");
        }

        return bestMove;
    }

    private int expectiminimax(Board board, int depth, boolean maximizingPlayer, Brick brick, int alpha, int beta) {
        if (depth >= MAX_DEPTH || board.isGameOver()) {
            // Always evaluate the board form the perspective of the AI
            return evaluateBoard(board, this.brick);
        }
        return new ExpectiminimaxTask(board, depth, maximizingPlayer, brick, alpha, beta).compute();
    }

    private class ExpectiminimaxTask extends RecursiveTask<Integer> {
        private final Board board;
        private final int depth;
        private final boolean maximizingPlayer;
        private final Brick brick;
        private int alpha;
        private int beta;

        public ExpectiminimaxTask(Board board, int depth, boolean maximizingPlayer, Brick brick, int alpha, int beta) {
            this.board = board;
            this.depth = depth;
            this.maximizingPlayer = maximizingPlayer;
            this.brick = brick;
            this.alpha = alpha;
            this.beta = beta;
        }

        @Override
        protected Integer compute() {
            int totalEval = 0;

            for (Roll roll : ALL_ROLLS) {
                List<Move[]> possibleMoves = board.actions(roll.values, brick);

                int bestEval = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                for (Move[] moveSequence : possibleMoves) {
                    Board simulatedBoard = board.clone();
                    for (Move move : moveSequence) {
                        simulatedBoard.performMove(move);
                    }

                    int eval = expectiminimax(simulatedBoard, depth + 1, !maximizingPlayer, brick.opponent(), alpha, beta);

                    if (maximizingPlayer) {
                        // Our turn, trying to maximise our score
                        bestEval = Math.max(bestEval, eval);
                        alpha = Math.max(alpha, eval);
                    } else {
                        // The opponent's turn, trying to minimise our score
                        bestEval = Math.min(bestEval, eval);
                        beta = Math.min(beta, eval);
                    }
                    // Prune if needed
                    if (alpha >= beta) { break; }
                }

                totalEval += bestEval * roll.weight;
            }

            return totalEval / NUM_ROLLS;
        }
    }

    private static int evaluateBoard(Board board, Brick brick) {
        int aiScore = 0;

        // Calculate blot hits for all possible roll
        aiScore += (int) Math.round((Evaluation.calculateBlotHitsForAllRolls(board, brick)/36.0)*21);

        // Calculate pip loss for all possible moves
        //aiScore += (int) (Math.sqrt(Evaluation.calculatePipLoss(board, brick)) / 10);

        // Add scores for pieces in the home board
        aiScore += (int) Math.round( (evaluateHomeBoard(board, brick)/69.0)*21);

        // Step 5: Blockade Evaluation
        aiScore += (int) Math.round((evaluateBlockades(board, brick)/98.0)*18);

        // Add scores for pieces borne off

        aiScore += (int) Math.round((board.getWinTrayCount(brick)/15.0)*15);

        // Prioritize stacking pieces
        aiScore += (int) Math.round((evaluateStacking(board, brick)/90.0)*26);

        // Check if the home board count is 15 to prioritize bearing off
        if (board.getHomeBoardCount(brick) == 15) {
            // Boost the bearing off priority score when the home board count is 15
            aiScore += 100;
        }


        return aiScore;
    }
    // Provide
    private static int evaluateBlockades(Board board, Brick brick) {
        int blockadeScore = 0;
        int longestBlockade = 0;
        int currentBlockadeLength = 0;
        int opponentFarthestBack = -1;
        List<Integer> trappedPositions = new ArrayList<>();

        int start = brick == Brick.WHITE ? 0 : 23;
        int end = brick == Brick.WHITE ? 23 : 0;
        int step = brick == Brick.WHITE ? 1 : -1;

        // Find all opponent checkers behind the blockade
        for (int i = start; brick == Brick.WHITE ? i <= end : i >= end; i += step) {
            Board.Point point = board.board[i];
            if (point.brick() == brick.opponent()) {
                trappedPositions.add(i);
                opponentFarthestBack = i;  // Track the farthest-back opponent
            }
        }

        // Identify the longest blockade by iterating through the board and finding adjacent columns with more than one stone
        for (int i = 0; i < 24; i++) {
            Board.Point point = board.board[i];

            if (point.brick() == brick && point.count() > 1) {
                currentBlockadeLength++;
            } else {
                longestBlockade = Math.max(longestBlockade, currentBlockadeLength);
                currentBlockadeLength = 0;
            }
        }

        // If a blockade exists, compute its strength
        if (longestBlockade >= 2) {
            int blockadeStrength = longestBlockade * longestBlockade; // Square to emphasize longer blockades

            // Check if opponent is actually trapped
            if (!trappedPositions.isEmpty() && trappedPositions.get(0) < longestBlockade) {
                blockadeStrength *= 2;  // Double the bonus if the blockade is effective
            }

            // Compute escape difficulty for all trapped checkers
            int escapeRolls = calculateEscapeRolls(board, brick.opponent(), trappedPositions, longestBlockade);
            int containmentValue = 36 - escapeRolls; // More containment = higher value

            blockadeScore += blockadeStrength + containmentValue;
        }

        return blockadeScore;
    }

    private static int calculateEscapeRolls(Board board, Brick opponentBrick, List<Integer> trappedPositions, int blockadeEnd) {
        int totalEscapeRolls = 0;

        // Simulate all dice rolls (1-6, 1-6)
        for (int die1 = 1; die1 <= 6; die1++) {
            for (int die2 = 1; die2 <= 6; die2++) {

                // Check if any trapped checker can escape by using the dice rolls (in any order)
                for (int position : trappedPositions) {
                    // Move by die1, then move by die2
                    int firstMove = position + die1;
                    if (firstMove < 24) {
                        Board.Point firstPoint = board.board[firstMove];
                        if (firstPoint.brick() != opponentBrick || firstPoint.count() < 2) {
                            // If the first move is valid, try the second move (by die2)
                            int secondMove = firstMove + die2;
                            if (secondMove < 24) {
                                Board.Point secondPoint = board.board[secondMove];
                                if (secondPoint.brick() != opponentBrick || secondPoint.count() < 2) {
                                    totalEscapeRolls++;  // Count the valid escape
                                    break;  // Only count the best escape roll for a given move
                                }
                            }
                        }
                    }
                }
            }
        }

        return totalEscapeRolls;
    }
    private static int evaluateStacking(Board board, Brick brick) {
        int stackingScore = 0;
        int[] anchorPoints = brick == Brick.BLACK ? new int[]{18, 19, 20} : new int[]{5, 4, 3};

        for (int i = 0; i < board.board.length; i++) {
            Board.Point point = board.board[i];
            final int index = i; // Make the variable effectively final
            if (point.brick() == brick) {
                if (point.count() >= 2) {
                    stackingScore += 10; // Reward each stack equally
                }
                if (point.count() > 2 && Arrays.stream(anchorPoints).anyMatch(ap -> ap == index)) {
                    stackingScore += (index == anchorPoints[0] ? 30 : 20); // Stronger anchor points get higher scores
                }
            }
        }
        return stackingScore;
    }

    private static int evaluateHomeBoard(Board board, Brick brick) {
        int homeBoardScore = 0;
        int start = brick == Brick.WHITE ? 18 : 0;
        int end = brick == Brick.WHITE ? 23 : 5;
        int step = 1;

        for (int i = start; i <= end; i += step) {
            Board.Point point = board.board[i];
            if (point.brick() == brick) {
                int distanceToEdge = brick == Brick.WHITE ? 23 - i : i;
                homeBoardScore += point.count() * (distanceToEdge + 1);
            }
        }

        return homeBoardScore;
    }

    @Override
    public String getName() {
        return "AI";
    }

    public record Roll(int weight, List<Integer> values) {}

    static {
        ALL_ROLLS = new Roll[21];
        int index = 0;
        for (int i = 1; i <= 6; i++) {
            ALL_ROLLS[index++] = new Roll(1, List.of(i, i, i, i));
        }
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j < i; j++) {
                ALL_ROLLS[index++] = new Roll(2, List.of(i, j));
            }
        }
    }
}
