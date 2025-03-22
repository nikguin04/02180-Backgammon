package com.dtu.backgammon.ai;

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
                        hits++;
                        break newroll;
                    }
                }
            }
        }
        return hits;
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
