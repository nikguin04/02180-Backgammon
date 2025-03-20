package com.dtu.backgammon.ai;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Move;
import com.dtu.backgammon.player.Player;

import java.util.List;

public class AI_MonteCarlo extends Player {

    public AI_MonteCarlo(Board.Brick brick) {
        super(brick);
    }

    public Move[] getMove(Board board, List<Integer> roll) {
        // Use MCTS to get the best sequence of moves
        return MonteCarlo.getBestMove(board, roll, brick);
    }

    public String getName() {
        return "AI_MonteCarlo";
    }



}
