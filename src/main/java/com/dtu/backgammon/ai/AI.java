package com.dtu.backgammon.ai;

import java.util.List;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.player.Player;

public class AI extends Player {
    private static final int MAX_DEPTH = 3;

    public AI(Brick brick) {
        super(brick);
    }

    @Override
    public Move getMove(Board board, List<Integer> roll) {
        Board boardClone = board.clone();
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        // Generate all possible move sequences
        List<Move[]> possibleMoves = boardClone.actions(roll, brick);

        for (Move[] moveSequence : possibleMoves) {
            Board simulatedBoard = boardClone.clone();

            // Simulate the moves on the cloned board
            for (Move move : moveSequence) {
                simulatedBoard.performMove(move);
            }

            // Evaluate the board state after the sequence of moves
            int moveValue = EvaluateBoard(simulatedBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            // Select the best move based on the evaluated value
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = moveSequence[0]; // Select the first move of the best sequence
            }
        }

        if (bestMove == null) {
            throw new IllegalStateException("No valid move found!");
        }

        return bestMove;
    }


    @Override
    public String getName() {
        return "AI";
    }



}
