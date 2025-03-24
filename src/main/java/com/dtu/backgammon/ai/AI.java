package com.dtu.backgammon.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Logger;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.ai.Evaluation;
import com.dtu.backgammon.player.Player;

public class AI extends Player {
    private static final int MAX_DEPTH = 4;
    public static final Roll[] ALL_ROLLS;
    public static final int NUM_ROLLS = 6 * 6;

    private static final ForkJoinPool pool = new ForkJoinPool(); // Global thread pool

    public AI(Brick brick) {
        super(brick);
    }

    @Override
    public Move[] getMove(Board board, List<Integer> roll) {
        Board boardClone = board.clone();
        List<Move[]> possibleMoves = boardClone.actions(roll, brick);

        if (possibleMoves.isEmpty()) {
            throw new IllegalStateException("No valid move found!");
        }

        List<ExpectiminimaxTask> tasks = new ArrayList<>();

        // Start all branches in parallel, each child of MAX node
        for (Move[] moveSequence : possibleMoves) {
            Board simulatedBoard = boardClone.clone();
            for (Move move : moveSequence) {
                simulatedBoard.performMove(move);
            }

            tasks.add(new ExpectiminimaxTask(
                    simulatedBoard,
                    1,
                    NodeType.CHANCE, // MAX node already handled here
                    brick,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE,
                    moveSequence
            ));
        }

        // Collect results
        Move[] bestMove = null;
        int bestValue = Integer.MIN_VALUE;
        for (ExpectiminimaxTask task : tasks) {
            task.fork();
        }
        for (ExpectiminimaxTask task : tasks) {
            int eval = task.join();
            if (eval > bestValue) {
                bestValue = eval;
                bestMove = task.moveSequence; // Use the move sequence from the task itself
            }
        }

        return bestMove;
    }

    private enum NodeType {
        MAX, MIN, CHANCE
    }

    public record Roll(int weight, List<Integer> values) {}

    private class ExpectiminimaxTask extends RecursiveTask<Integer> {
        private final Board board;
        private final int depth;
        private final NodeType nodeType;
        private final Brick brick;
        private int alpha;
        private int beta;
        private final Move[] moveSequence; // Only needed at the root level

        public ExpectiminimaxTask(Board board, int depth, NodeType nodeType, Brick brick, int alpha, int beta, Move[] moveSequence) {
            this.board = board;
            this.depth = depth;
            this.nodeType = nodeType;
            this.brick = brick;
            this.alpha = alpha;
            this.beta = beta;
            this.moveSequence = moveSequence;
        }

        @Override
        protected Integer compute() {
            if (depth >= MAX_DEPTH || board.isGameOver()) {
                return evaluateBoard(board, AI.this.brick);
            }

            switch (nodeType) {
                case CHANCE:
                    return handleChanceNode();
                case MAX:
                case MIN:
                    throw new IllegalStateException("MAX or MIN node must be called with a roll value");
                default:
                    throw new IllegalStateException("Unknown node type");
            }
        }

        private int handleChanceNode() {
            int totalEval = 0;
            NodeType nextNodeType = (depth % 2 == 0) ? NodeType.MAX : NodeType.MIN;
            for (Roll roll : ALL_ROLLS) {
                Board rollSimulatedBoard = board.clone();
                int eval = new ExpectiminimaxTask(
                        rollSimulatedBoard,
                        depth + 1,
                        nextNodeType,
                        brick,
                        alpha,
                        beta,
                        null // still root level, no sequence needed
                ).computeWithRoll(roll);
                totalEval += (eval * roll.weight) / NUM_ROLLS;
            }
            return totalEval;
        }

        private int computeWithRoll(Roll roll) {
            if (depth >= MAX_DEPTH || board.isGameOver()) {
                return evaluateBoard(board, AI.this.brick);
            }

            switch (nodeType) {
                case MAX:
                    return handleMaxNode(roll);
                case MIN:
                    return handleMinNode(roll);
                default:
                    throw new IllegalStateException("Chance node should not call computeWithRoll");
            }
        }

        private int handleMaxNode(Roll roll) {
            int bestEval = Integer.MIN_VALUE;
            List<Move[]> possibleMoves = board.actions(roll.values, brick); // Pass roll values

            if (possibleMoves.isEmpty()) {
                return evaluateBoard(board, AI.this.brick);
            }

            for (Move[] moveSequence : possibleMoves) {
                Board simulatedBoard = board.clone();
                for (Move move : moveSequence) {
                    simulatedBoard.performMove(move);
                }

                int eval = new ExpectiminimaxTask(
                        simulatedBoard,
                        depth + 1,
                        NodeType.CHANCE,
                        brick.opponent(),
                        alpha,
                        beta,
                        null
                ).fork().join();

                bestEval = Math.max(bestEval, eval);
                alpha = Math.max(alpha, eval);

                if (alpha >= beta) break; // Alpha-beta pruning
            }
            return bestEval;
        }

        private int handleMinNode(Roll roll) {
            int bestEval = Integer.MAX_VALUE;
            List<Move[]> possibleMoves = board.actions(roll.values, brick); // Pass roll values

            if (possibleMoves.isEmpty()) {
                return evaluateBoard(board, AI.this.brick);
            }

            for (Move[] moveSequence : possibleMoves) {
                Board simulatedBoard = board.clone();
                for (Move move : moveSequence) {
                    simulatedBoard.performMove(move);
                }

                int eval = new ExpectiminimaxTask(
                        simulatedBoard,
                        depth + 1,
                        NodeType.CHANCE,
                        brick.opponent(),
                        alpha,
                        beta,
                        null
                ).fork().join();

                bestEval = Math.min(bestEval, eval);
                beta = Math.min(beta, eval);

                if (alpha >= beta) break; // Alpha-beta pruning
            }
            return bestEval;
        }

        private static int evaluateBoard(Board board, Brick brick) {
            int aiScore = 0;

            // Calculate blot hits for all possible roll
            int blothits =(int) Math.round((Evaluation.calculateBlotHitsForAllRolls(board, brick)/21.0)*16);
            aiScore += blothits;

            // Calculate pip loss for all possible moves
            int piploss =(int) Math.round((Evaluation.calculatePipLoss(board, brick.opponent())/135.0)*15);
            aiScore += piploss;

            // Add scores for pieces in the home board
            int homeboard = (int) Math.round((Evaluation.evaluateHomeBoard(board, brick)/75.0)*20);
            aiScore += homeboard;

            // Step 5: Blockade Evaluation
            int blockades = (int) Math.round((Evaluation.evaluateBlockades(board, brick)/98.0)*10);
            aiScore += blockades;

            // Add scores for pieces borne off
            int wintray = (int) Math.round((board.getWinTrayCount(brick)/15.0)*10);
            aiScore += wintray;

            // Prioritize stacking pieces
            int stacking = (int) Math.round( (Evaluation.evaluateStacking(board, brick)/110.0)*29);
            aiScore += stacking;

            // Check if the home board count is 15 to prioritize bearing off
            if (board.getHomeBoardCount(brick) == 15) {
                // Boost the bearing off priority score when the home board count is 15
                aiScore += 100;
            }
            Logger.eval(brick, blothits, piploss, homeboard, blockades, wintray, stacking);

            return aiScore;
        }
    }

        

    @Override
    public String getName() {
        return "AI";
    }

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