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

    public AI(Brick brick) {
        super(brick);
    }

    @Override
    // Figure out the best first move, and then find the highest eval move
    public Move getMove(Board board, List<Integer> roll) {
        Board boardClone = board.clone();
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        List<Move[]> possibleMoves = boardClone.actions(roll, brick);

        //For each move in possible moves, clone the board and perform the move
        for (Move[] moveSequence : possibleMoves) {

            Board simulatedBoard = boardClone.clone();
            for (Move move : moveSequence) {
                simulatedBoard.performMove(move);
            }

            int moveValue = expectiminimax(simulatedBoard, MAX_DEPTH, false, brick);

            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = moveSequence[0];
            }
        }

        if (bestMove == null) {
            throw new IllegalStateException("No valid move found!");
        }

        return bestMove;
    }


    private static int expectiminimax(Board board, int depth, boolean maximizingPlayer, Brick brick) {
        if (depth >= MAX_DEPTH || board.isGameOver()) {
            return evaluateBoard(board, brick);
        }

        List<int[]> possibleRolls = generatePossibleRolls();
        int totalEval = 0;

        for (int[] roll : possibleRolls) {
            List<Move[]> possibleMoves = board.actions(Arrays.stream(roll).boxed().toList(), brick);

            if (maximizingPlayer) {
                int maxEval = Integer.MIN_VALUE;
                for (Move[] moveSequence : possibleMoves) {
                    Board simulatedBoard = board.clone();
                    for (Move move : moveSequence) {
                        simulatedBoard.performMove(move);
                    }
                    int eval = expectiminimax(simulatedBoard, depth + 1, false, brick);
                    maxEval = Math.max(maxEval, eval);
                }
                totalEval += maxEval;
            } else {
                int minEval = Integer.MAX_VALUE;
                for (Move[] moveSequence : possibleMoves) {
                    Board simulatedBoard = board.clone();
                    for (Move move : moveSequence) {
                        simulatedBoard.performMove(move);
                    }
                    int eval = expectiminimax(simulatedBoard, depth + 1, true, brick.opponent());
                    minEval = Math.min(minEval, eval);
                }
                totalEval += minEval;
            }
        }

        return totalEval / possibleRolls.size();
    }

    private static List<int[]> generatePossibleRolls() {
        List<int[]> possibleRolls = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
                possibleRolls.add(new int[]{i, j});
            }
        }
        return possibleRolls;
    }

    public static List<int[]> generatePossibleRollsNonDupe() {
        List<int[]> possibleRolls = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                possibleRolls.add(new int[]{i, j});
            }
        }
        return possibleRolls;
    }

    private static int evaluateBoard(Board board, Brick brick) {
        int aiScore = 0;
        int opponentScore = 0;

        for (int i = 0; i < board.getPoints().length; i++) {
            Board.BoardElement point = board.getPoints()[i];
            if (point.getBrick() == brick) {
                aiScore += point.getCount();
                if (point.getCount() > 1) {
                    aiScore += 5; // Blockade bonus
                }
            } else if (point.getBrick() != null) {
                opponentScore += point.getCount();
                if (point.getCount() > 1) {
                    opponentScore += 5; // Blockade bonus
                }
            }
        }

        // Add scores for pieces on the bar
        aiScore -= board.getBarCount(brick) * 2;
        opponentScore -= board.getBarCount(brick.opponent()) * 2;

        // Add scores for pieces in the home board
        aiScore += board.getHomeBoardCount(brick) * 2;
        opponentScore += board.getHomeBoardCount(brick.opponent()) * 2;

        // Step 5: Blockade Evaluation
        aiScore += evaluateBlockades(board, brick);
        opponentScore += evaluateBlockades(board, brick.opponent());

        // Add scores for pieces borne off
        aiScore += board.getWinTrayCount(brick) * 10;
        opponentScore += board.getWinTrayCount(brick.opponent()) * 10;

        return aiScore - opponentScore;
    }

    private static int evaluateBlockades(Board board, Brick brick) {
        int blockadeScore = 0;
        int longestBlockade = 0;
        int currentBlockadeLength = 0;
        int escapeRolls = 36; // Maximum possible dice combinations
        int opponentFarthestBack = -1;
        int start = brick == Brick.WHITE ? 0 : 23;  // White starts at 0, Black starts at 23
        int end = brick == Brick.WHITE ? 23 : 0;    // White stops at 23, Black stops at 0
        int step = brick == Brick.WHITE ? 1 : -1;   // White moves forward, Black moves backward

    // Find the furthest-back checker of the opponent
        for (int i = start; brick == Brick.WHITE ? i <= end : i >= end; i += step) {
            Board.BoardElement point = board.getPoints()[i];
            if (point.getBrick() == brick.opponent()) {
                opponentFarthestBack = i;
                break;
            }
        }


        // Iterate through the board to detect blockades
        for (int i = 0; i < 24; i++) {
            Board.BoardElement point = board.getPoints()[i];

            if (point.getBrick() == brick && point.getCount() > 1) {
                currentBlockadeLength++;
            } else {
                if (currentBlockadeLength > 1) {
                    longestBlockade = Math.max(longestBlockade, currentBlockadeLength);
                }
                currentBlockadeLength = 0;
            }
        }

        // Assign blockade strength based on length
        if (longestBlockade >= 2) {
            int blockadeStrength = longestBlockade * longestBlockade; // Square to emphasize longer blockades

            // Check if opponent is trapped behind blockade
            if (opponentFarthestBack != -1 && opponentFarthestBack < longestBlockade) {
                blockadeStrength *= 2; // Double bonus if opponent is actually trapped
            }

            // Reduce score based on escape difficulty
            escapeRolls = calculateEscapeRolls(board, brick.opponent(), longestBlockade);
            int containmentValue = 36 - escapeRolls;

            blockadeScore += blockadeStrength + containmentValue;
        }

        return blockadeScore;
    }

    private static int calculateEscapeRolls(Board board, Brick opponentBrick, int blockadeEnd) {
        int escapeRolls = 0;

        // Simulate all dice rolls (1-6, 1-6)
        for (int die1 = 1; die1 <= 6; die1++) {
            for (int die2 = 1; die2 <= 6; die2++) {
                int rollSum = die1 + die2;
                int escapePoint = blockadeEnd + rollSum;

                if (escapePoint < 24) {
                    Board.BoardElement point = board.getPoints()[escapePoint];
                    if (point.getBrick() != opponentBrick || point.getCount() < 2) {
                        escapeRolls++;
                    }
                }
            }
        }

        return escapeRolls;
    }


    @Override
    public String getName() {
        return "AI";
    }



}
