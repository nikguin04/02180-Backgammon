package com.dtu.backgammon.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dtu.backgammon.Board;
import com.dtu.backgammon.Board.BoardElement;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Move;

public class AI extends Player {

    public AI(Brick brick) {
        super(brick);
    }

    @Override
    public void getMove() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMove'");
    }

    @Override
    public String getName() {
        return "AI";
    }
    

    public List<Move[]> actions(Board board, List<Integer> diceMoves) { // This returns an array of some amount of moves to perform. This means that the return in ALL possible moves 
        List<BoardElement> boardElements = board.board;
        Brick player = brick;
        List<List<Integer>> diceMovesAnyOrder = new ArrayList<>();
        if (diceMoves.size() < 2 || diceMoves.get(0) == diceMoves.get(1)) { // All eyes are equal
            diceMovesAnyOrder.add(diceMoves);
        } else {
            // Scuffed way of reversing dice moves
            diceMovesAnyOrder.add(new ArrayList<>( Arrays.asList(new Integer[] {diceMoves.get(0), diceMoves.get(1)}) ));
            diceMovesAnyOrder.add(new ArrayList<>( Arrays.asList(new Integer[] {diceMoves.get(1), diceMoves.get(0)}) ));
        }

        List<Move[]> moves = new ArrayList<Move[]>();
        for (List<Integer> diceMove : diceMovesAnyOrder) {

            for (int i = 0; i < boardElements.size(); i++) {
                if (boardElements.get(i).brick == player) { // The current player has bricks to move here
                    Move move;
                    if (board.hasBrickInTray(player)) { // Player has brick in the tray and needs to move them out first.
                        int startPos = player == Brick.BLACK ? Board.BLACK_START : Board.WHITE_START;
                        int toPosition = startPos + diceMove.get(0) * (player == Brick.BLACK ? -1 : 1); // Calculate the absolut to position for reentry
                        move = new Move(startPos, toPosition, Move.MoveType.REENTRY);
                        i = boardElements.size(); // Make sure to break loop if we have a brick in tray.
                    } else {
                        int toPosition = i + diceMove.get(0) * (player == Brick.BLACK ? -1 : 1);
                        Move.MoveType movetype = toPosition > 23 || toPosition < 0 ? Move.MoveType.BEARINGOFF : Move.MoveType.NORMAL; // Players can only move in their right direction which means this logic works for either player.
                        move = new Move(i, toPosition, movetype); // Goes 0->23 for white and 23->0 for black
                    }

                    if (move.to() > 23 || move.to() < 0) { continue; }
                    if (!board.isValidMove(move, player, diceMove.get(0))) { continue; } // Move is not valid

                    List<Integer> newDiceMoves = new ArrayList<>(diceMove);
                    newDiceMoves.remove(0);
                    
                    if (newDiceMoves.size() > 0) {
                        Board newBoard = board.clone();
                        newBoard.performMove(move);
                        List<Move[]> actions = this.actions(board, newDiceMoves);
                        for (int j = 0; j < actions.size(); j++) {
                            List<Move> nestMoves = new ArrayList<>(Arrays.asList(actions.get(j)));
                            nestMoves.add(0, move);
                            moves.add( nestMoves.toArray(Move[]::new) ); // Add list of new moves at this state to the moves array
                        }
                    } else {
                        moves.add(new Move[] { move });
                    }
                }
            }
        }
        return moves;
    }
    
}
