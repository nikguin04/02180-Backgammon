package com.dtu.backgammon.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Dice;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.Board.Brick;

public class Evaluation {

    public static int calculateBlotHitsForAllRolls(Board board, Brick brick) {
        int hits = 0;
        List<Integer> blotCols = board.getBlots(brick);
        int dir = brick == Brick.WHITE ? Board.WHITE_DIR : Board.BLACK_DIR; // When brick moves towards its own direction, the dice rolls that are needed for oppenent to hit our player is equal to the amount we need to hit him
        
        for (AI.Roll roll : AI.ALL_ROLLS) {
            newroll:
            for (Integer blotCol: blotCols) {
                for (int dist: new Dice(roll.values().get(0), roll.values().get(1)).getPossibleProgression() ) { // Gets possbile progression for roll (needs to convert to dice)
                    int fromPos = blotCol + dist * dir;

                    if (fromPos > -1 && fromPos < 24 && board.board[fromPos].brick() == brick.opponent()) { // Frompos within range and contains enemy
                        hits += roll.weight();
                        break newroll;
                    }
                }
            }
        }
        return hits;
    }


    public static int calculatePipLoss(Board board, Brick brick) {
        double totalPiploss = 0;
        List<Integer> blotCols = board.getBlots(brick);
        int dir = brick == Brick.WHITE ? Board.WHITE_DIR : Board.BLACK_DIR; // When brick moves towards its own direction, the dice rolls that are needed for oppenent to hit our player is equal to the amount we need to hit him


        for (Integer blotCol: blotCols) {
            newblot:
            for (AI.Roll roll : AI.ALL_ROLLS) {
                newroll:
                for(int dist: new Dice(roll.values().get(0), roll.values().get(1)).getPossibleProgression() ) { // Gets possbile progression for roll (needs to convert to dice)
                    int fromPos = blotCol + dist * dir;

                    if (fromPos > -1 && fromPos < 24 && board.board[fromPos].brick() == brick.opponent()) { // Frompos within range and contains enemy
                        totalPiploss += (double)roll.weight()/36 * (brick == Brick.WHITE ? fromPos : 23-fromPos); // Return weighed x piploss for white, or 23-x piploss for black (reversed direction)
                        break newroll;
                    }
                }
            }
        }
        return (int)Math.ceil(totalPiploss);
    }

    public static int evaluateBlockades(Board board, Brick brick) {
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

        public static int evaluateStacking(Board board, Brick brick) {
            int stackingScore = 0;
            int[] anchorPoints = brick == Brick.BLACK ? new int[]{18, 19, 20} : new int[]{5, 4, 3};

            for (int i = 0; i < board.board.length; i++) {
                Board.Point point = board.board[i];
                final int index = i; // Make the variable effectively final
                if (point.brick() == brick) {
                    if (point.count() >= 2) {
                        stackingScore += 10; // Reward each stack equally
                    }
                    if (point.count() >= 2 && Arrays.stream(anchorPoints).anyMatch(ap -> ap == index)) {
                        stackingScore += (index == anchorPoints[0] ? 30 : 20); // Stronger anchor points get higher scores
                    }
                }
            }
            return stackingScore;
        }

        public static int evaluateHomeBoard(Board board, Brick brick) {
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
}
