package com.dtu.backgammon.ai;

import java.util.List;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.Board.Brick;

public class Evaluation {

    public static int calculateBlotHitsForAllRolls(Board board, Brick brick) {
        int hits = 0;
        for (AI.Roll roll : AI.ALL_ROLLS) {
            hits += roll.weight() * calculateIfBlotHitForRoll(board, brick, roll.values());
        }

        return hits;
    }

    public static int calculateIfBlotHitForRoll(Board board, Brick brick, List<Integer> rolls) {
        Brick oppositeBrick = brick == Brick.BLACK ? Brick.WHITE : Brick.BLACK;
        List<Move[]> actions = board.actions(rolls, brick);

        for (Move[] action : actions) {
            Board newBoard = board.clone();
            int startBar = newBoard.getBarCount(oppositeBrick);
            newBoard.performMoves(action);
            int diffBar = newBoard.getBarCount(oppositeBrick) - startBar;
            if (diffBar > 0) { return 1; }
        }

        return 0;
    }

    public static int calculateTotalBlotPipLossForRoll(Board board, Brick brick, List<Integer> rolls) {
        Brick oppositeBrick = brick.opponent();
        int piploss = 0;
        List<Move[]> actions = board.actions(rolls, brick);

        for (Move[] action : actions) {
            Board newBoard = board.clone();
            int startScore = newBoard.getTotalBrickProgress(oppositeBrick);
            newBoard.performMoves(action);
            int diffBar = startScore - newBoard.getTotalBrickProgress(oppositeBrick);
            piploss += diffBar;
        }

        return piploss;
    }

    public static int calculatePipLoss(Board board, Brick brick) {
        int totalPiploss = 0;
        for (AI.Roll roll : AI.ALL_ROLLS) {
            totalPiploss += roll.weight() * calculateTotalBlotPipLossForRoll(board, brick, roll.values());
        }

        return totalPiploss;
    }
}
