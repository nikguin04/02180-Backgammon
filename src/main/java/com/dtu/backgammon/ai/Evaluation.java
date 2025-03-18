package com.dtu.backgammon.ai;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.player.Player;

public class Evaluation {

    public static int calculateBlotHitsForAllRolls(Board board, Brick brick) {
        int hits = 0;
        List<int[]> possibleRolls = AI.generatePossibleRollsNonDupe();
        for (int[] roll : possibleRolls) {
            hits += calculateIfBlotHitForRoll(board, brick, roll);
        }

        return hits;
    }

    public static int calculateIfBlotHitForRoll(Board board, Brick brick, int[] rolls) {
        Brick oppositeBrick = brick == Brick.BLACK ? Brick.WHITE : Brick.BLACK;
        List<Move[]> actions = board.actions(Arrays.stream(rolls).boxed().collect(Collectors.toList()), brick); // Note: might not work due to not being an ArrayList

        for (Move[] action : actions) {
            Board newBoard = board.clone();
            int startBar = newBoard.getBarCount(oppositeBrick);
            newBoard.performMoves(action);
            int diffBar = newBoard.getBarCount(oppositeBrick) - startBar;
            if (diffBar > 0) { return 1; }
        }

        return 0;
    }

    public static int calculateTotalBlotPipLossForRoll(Board board, Brick brick, int[] rolls) {
        Brick oppositeBrick = brick.opponent();
        int piploss = 0;
        List<Move[]> actions = board.actions(Arrays.stream(rolls).boxed().collect(Collectors.toList()), brick); // Note: might not work due to not being an ArrayList

        for (Move[] action : actions) {
            Board newBoard = board.clone();
            int startScore = newBoard.getScore(oppositeBrick);
            newBoard.performMoves(action);
            int diffBar = startScore - newBoard.getScore(oppositeBrick);
            piploss += diffBar;
        }

        return piploss;
    }

    public static int calculatePipLoss(Board board, Brick brick) {
        List<int[]> possibleRolls = AI.generatePossibleRollsNonDupe();

        int totalPiploss = 0;
        for (int[] roll : possibleRolls) {
            totalPiploss += calculateTotalBlotPipLossForRoll(board, brick, roll);
        }

        return totalPiploss;
    }
}
