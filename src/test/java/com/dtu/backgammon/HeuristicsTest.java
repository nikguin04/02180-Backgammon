package com.dtu.backgammon;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dtu.backgammon.Board.BoardElement;
import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.ai.AI;
import com.dtu.backgammon.ai.Heuristics;
import com.dtu.backgammon.player.Player;

/**
 * Unit test for simple App.
 */
public class HeuristicsTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void CalculateBlotHitsForRollWhite() {

        List<Player> players = new ArrayList<>();
        Player whitep = new AI(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI(Brick.BLACK);
        players.add(blackp);

        //Dice dice = new Dice(2,3);
        int[][] rolls = {
            {2,3},
            {5,6},
            {6,6},
            {1,1,1,1},
            {3,4}
        };

        List<BoardElement> boardelems = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            boardelems.add(i, new BoardElement(Brick.NONE, 0));
        }
        Board board = new Board(boardelems, players);
        board.setColumn(10, Brick.WHITE, 1);
        board.setColumn(14, Brick.WHITE, 1);

        board.setColumn(12, Brick.BLACK, 1);
        board.setColumn(13, Brick.BLACK, 1);
        board.setColumn(17, Brick.BLACK, 1);

        int hits = Heuristics.CalculateBlotHitsForRolls(board, whitep, rolls);
        Assertions.assertEquals(3, hits);
    }


    @Test
    public void CalculatePiplossBlackSingleMove() {

        List<Player> players = new ArrayList<>();
        Player whitep = new AI(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI(Brick.BLACK);
        players.add(blackp);

        List<BoardElement> boardelems = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            boardelems.add(i, new BoardElement(Brick.NONE, 0));
        }
        Board board = new Board(boardelems, players);
        board.setColumn(10, Brick.WHITE, 1);

        board.setColumn(12, Brick.BLACK, 1);
        board.setColumn(13, Brick.BLACK, 1);

        int[] roll = {2,3};
        int piploss = Heuristics.CalculateTotalBlotPiplossForRoll(board, blackp, roll);
        Assertions.assertEquals(27, piploss);
    }


    @Test
    public void CalculatePiplossBlackAll() {

        List<Player> players = new ArrayList<>();
        Player whitep = new AI(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI(Brick.BLACK);
        players.add(blackp);

        List<BoardElement> boardelems = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            boardelems.add(i, new BoardElement(Brick.NONE, 0));
        }
        Board board = new Board(boardelems, players);
        board.setColumn(10, Brick.WHITE, 1);
        //board.setColumn(14, Brick.WHITE, 1);

        //board.setColumn(12, Brick.BLACK, 1);
        //board.setColumn(13, Brick.BLACK, 1);
        board.setColumn(17, Brick.BLACK, 1);


        int piploss = Heuristics.CalculatePipLoss(board, blackp);
        Assertions.assertEquals(84, piploss);
    }
}
