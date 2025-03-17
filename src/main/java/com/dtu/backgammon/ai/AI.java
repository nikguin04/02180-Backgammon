package com.dtu.backgammon.ai;

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
    public Move getMove(Board board, List<Integer> roll) {
        Board boardClone = board.clone();
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        List<Move[]> possibleMoves = boardClone.actions(roll, brick);

        for (Move[] moveSequence : possibleMoves) {
            Board simulatedBoard = boardClone.clone();
            for (Move move : moveSequence) {
                simulatedBoard.performMove(move);
            }

            int moveValue = expectimax(simulatedBoard,roll, MAX_DEPTH, false,brick);

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


    private static int expectimax(Board board,List<Integer> roll, int depth, boolean maximizingPlayer,Brick brick) {
        if (depth == 0 || board.isGameOver()) {
            return evaluateBoard(board,brick);
        }

        List<Move[]> possibleMoves = board.actions(roll, brick);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move[] moveSequence : possibleMoves) {
                Board simulatedBoard = board.clone();
                for (Move move : moveSequence) {
                    simulatedBoard.performMove(move);
                }
                int eval = expectimax(simulatedBoard,roll, depth - 1, false,brick);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int totalEval = 0;
            for (Move[] moveSequence : possibleMoves) {
                Board simulatedBoard = board.clone();
                for (Move move : moveSequence) {
                    simulatedBoard.performMove(move);
                }
                int eval = expectimax(simulatedBoard,roll, depth - 1, true,brick);
                totalEval += eval;
            }
            return totalEval / possibleMoves.size();
        }
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

        // Add scores for pieces borne off
        aiScore += board.getWinTrayCount(brick) * 10;
        opponentScore += board.getWinTrayCount(brick.opponent()) * 10;

        return aiScore - opponentScore;
    }

    @Override
    public String getName() {
        return "AI";
    }



}
