package com.dtu.backgammon.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.player.Player;

public class AI extends Player {
    private static final int MAX_DEPTH = 2;
    public static final Roll[] ALL_ROLLS;
    public static final int NUM_ROLLS = 6 * 6;

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
        for (Move[] moveSequence : possibleMoves) {

            Board simulatedBoard = boardClone.clone();
            for (Move move : moveSequence) {
                simulatedBoard.performMove(move);
            }

            // This function acts as depth 0, so start expectiminimax at depth 1
            int moveValue = expectiminimax(simulatedBoard, 1, false, brick);

            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = moveSequence;
            }
        }

        if (bestMove == null) {
            throw new IllegalStateException("No valid move found!");
        }

        return bestMove;
    }

    private int expectiminimax(Board board, int depth, boolean maximizingPlayer, Brick brick) {
        if (depth >= MAX_DEPTH || board.isGameOver()) {
            // Always evaluate the board form the perspective of the AI
            return evaluateBoard(board, this.brick);
        }

        int totalEval = 0;

        for (Roll roll : ALL_ROLLS) {
            List<Move[]> possibleMoves = board.actions(roll.values, brick);

            if (maximizingPlayer) {
                // Our turn, trying to maximise our score
                int maxEval = Integer.MIN_VALUE;
                for (Move[] moveSequence : possibleMoves) {
                    Board simulatedBoard = board.clone();
                    for (Move move : moveSequence) {
                        simulatedBoard.performMove(move);
                    }
                    int eval = expectiminimax(simulatedBoard, depth + 1, false, brick.opponent());
                    maxEval = Math.max(maxEval, eval);
                }
                totalEval += maxEval * roll.weight;
            } else {
                // The opponent's turn, trying to minimise our score
                int minEval = Integer.MAX_VALUE;
                for (Move[] moveSequence : possibleMoves) {
                    Board simulatedBoard = board.clone();
                    for (Move move : moveSequence) {
                        simulatedBoard.performMove(move);
                    }
                    int eval = expectiminimax(simulatedBoard, depth + 1, true, brick.opponent());
                    minEval = Math.min(minEval, eval);
                }
                totalEval += minEval * roll.weight;
            }
        }

        return totalEval / NUM_ROLLS;
    }

    private static int evaluateBoard(Board board, Brick brick) {
        int aiScore = 0;

        // Calculate blot hits for all possible roll
       // aiScore += Evaluation.calculateBlotHitsForAllRolls(board, brick)/20;

        // Calculate pip loss for for all possible moves
       // aiScore += Evaluation.calculatePipLoss(board, brick)/5;

        // Add scores for pieces in the home board
        //aiScore += evaluateHomeBoard(board, brick;

        // Step 5: Blockade Evaluation
        //aiScore += evaluateBlockades(board, brick);

        // Add scores for pieces borne off
        //aiScore += board.getWinTrayCount(brick) * 10;

        // Prioritize stacking pieces
        aiScore += evaluateStacking(board, brick);

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
                int rollSum = die1 + die2;

                // Check if any trapped checker can escape
                for (int position : trappedPositions) {
                    int escapePoint = position + rollSum;

                    if (escapePoint < 24) {
                        Board.Point point = board.board[escapePoint];
                        if (point.brick() != opponentBrick || point.count() < 2) {
                            totalEscapeRolls++;
                            break;  // Only count the best escape roll for a given move
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
