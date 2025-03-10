package com.dtu.backgammon.player;

import java.util.Dictionary;
import java.util.Hashtable;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.Brick;

public class AI extends Player {

    public AI(Brick brick, Board board) {
        super(brick, board);
    }

    @Override
    public Move getMove() {
        findBestMove(board, board.getPossibleMoves(), 0, SearchType.MAX);
    }

    @Override
    public String getName() {
        return "AI";
    }

    class MoveHeuristics {
        Move move;
        int heuristics;
        public MoveHeuristics(Move move, int heuristics) {this.move = move; this.heuristics = heuristics;}
    }
    enum SearchType {MIN, MAX}

    private int maxDepth = 3;
    public MoveHeuristics findBestMove(Board board, Move[] possibleMoves, int depth, SearchType searchType) { // Book page 196
        List<MoveHeuristics> allHeuristics = new List<MoveHeuristics>();
        if (depth >= maxDepth) { // We are at the max defined depth, calculate the heristics for these moves
            for (Move move : possibleMoves) {
                allHeuristics.add(new MoveHeuristics(move, calculateHeuristics(board, move)));
            }
        }
        else { // Recursively call findBestMove for all moves (MINIMAX)
            Dictionary<Move, Integer> heuristics = new Hashtable<>();
            for (Move move : possibleMoves) {
                Move[] newMoves = board.getPossibleMoves(move);
                Board newBoard = board.clone() // WARNING: this can definitely lead to insane memory use
                newBoard.performMove(move);
                heuristics.put(move, findBestMove(newBoard, newMoves, depth+1, searchType == SearchType.MAX ? SearchType.MIN : SearchType.MAX));
            }
        }

        // Calculate the best heuristics for the search type, either min or max, then return it.
        MoveHeuristics bestHeuristics = MoveHeuristics(null, searchType == SearchType.MAX ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        for (Move move : possibleMoves) {
            int heuristics = calculateHeuristics(move);
            if (searchType == SearchType.MAX ? bestHeuristics.heuristics < heuristics : bestHeuristics.heuristics > heuristics) { // Make sure this new heristics is either bigger or smaller depending on search type
                bestHeuristics = MoveHeuristics(move, heuristics); // Here we set the max heuristics
            }
        }
        return bestHeuristics;
    }

    public int calculateHeuristics(Board board, Move move) { // Here we calculate something like the possibility of getting hit
        return Math.random()*100; // STUBBED
    }
    
}
