package com.dtu.backgammon;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Board.Point;
import com.dtu.backgammon.ai.AI_ExpectiMax;
import com.dtu.backgammon.ai.Evaluation;
import com.dtu.backgammon.player.Player;

/**
 * Unit test for the Evaluation class.
 */
public class EvaluationTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void calculateBlotHitsForRollWhite() {

        List<Player> players = new ArrayList<>();
        Player whitep = new AI_ExpectiMax(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI_ExpectiMax(Brick.BLACK);
        players.add(blackp);

        //Dice dice = new Dice(2,3);

        Point[] points = new Point[24];
        for (int i = 0; i < 24; i++) {
            points[i] = new Point(Brick.NONE, 0);
        }
        Board board = new Board(points, players);
        board.setColumn(10, Brick.WHITE, 1);
        board.setColumn(14, Brick.WHITE, 1);

        board.setColumn(12, Brick.BLACK, 1);
        board.setColumn(13, Brick.BLACK, 1);
        board.setColumn(17, Brick.BLACK, 1);

        int hits = Evaluation.calculateBlotHitsForAllRolls(board, whitep.brick);
        Assertions.assertEquals(13, hits);
    }


    @Test
    public void calculatePipLossBlackSingleMove() {

        List<Player> players = new ArrayList<>();
        Player whitep = new AI_ExpectiMax(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI_ExpectiMax(Brick.BLACK);
        players.add(blackp);

        Point[] points = new Point[24];
        for (int i = 0; i < 24; i++) {
            points[i] = new Point(Brick.NONE, 0);
        }
        Board board = new Board(points, players);
        board.setColumn(10, Brick.WHITE, 1);

        board.setColumn(12, Brick.BLACK, 1);
        board.setColumn(13, Brick.BLACK, 1);

        List<Integer> roll = List.of(2, 3);
        int pipLoss = Evaluation.calculateTotalBlotPipLossForRoll(board, whitep.brick, roll);
        Assertions.assertEquals(21, pipLoss);
    }


    @Test
    public void calculatePipLossBlackAll() {

        List<Player> players = new ArrayList<>();
        Player whitep = new AI_ExpectiMax(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI_ExpectiMax(Brick.BLACK);
        players.add(blackp);

        Point[] points = new Point[24];
        for (int i = 0; i < 24; i++) {
            points[i] = new Point(Brick.NONE, 0);
        }
        Board board = new Board(points, players);
        board.setColumn(10, Brick.WHITE, 1);
        //board.setColumn(14, Brick.WHITE, 1);

        //board.setColumn(12, Brick.BLACK, 1);
        //board.setColumn(13, Brick.BLACK, 1);
        board.setColumn(17, Brick.BLACK, 1);


        int pipLoss = Evaluation.calculatePipLoss(board, blackp.brick);
        Assertions.assertEquals(60, pipLoss);
    }
}
