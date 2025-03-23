package com.dtu.backgammon;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.Board.Point;
import com.dtu.backgammon.ai.AI;
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
        Player whitep = new AI(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI(Brick.BLACK);
        players.add(blackp);

        //Dice dice = new Dice(2,3);

        Point[] points = new Point[24];
        for (int i = 0; i < 24; i++) {
            points[i] = new Point(Brick.NONE, 0);
        }
        Board board = new Board(points, players);
        board.setColumn(5, Brick.WHITE, 1);
        board.setColumn(15, Brick.WHITE, 1);

        board.setColumn(12, Brick.BLACK, 1);
        board.setColumn(13, Brick.BLACK, 1);
        board.setColumn(17, Brick.BLACK, 1);

        int hits = Evaluation.calculateBlotHitsForAllRolls(board, whitep.brick.opponent());
        assertEquals(21, hits);
    }


    @Test
    public void calculatePipLossBlackAll() {

        List<Player> players = new ArrayList<>();
        Player whitep = new AI(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI(Brick.BLACK);
        players.add(blackp);

        Point[] points = new Point[24];
        for (int i = 0; i < 24; i++) {
            points[i] = new Point(Brick.NONE, 0);
        }
        Board board = new Board(points, players);
        board.setColumn(10, Brick.WHITE, 1);
        board.setColumn(14, Brick.WHITE, 1);

        //board.setColumn(12, Brick.BLACK, 1);
        board.setColumn(13, Brick.BLACK, 1);
        board.setColumn(17, Brick.BLACK, 1);


        int pipLoss = Evaluation.calculatePipLoss(board, blackp.brick);
        assertEquals(10, pipLoss);
    }

    @Test
    public void calculatePipLossWhiteAll() {

        List<Player> players = new ArrayList<>();
        Player whitep = new AI(Brick.WHITE);
        players.add(whitep);
        Player blackp = new AI(Brick.BLACK);
        players.add(blackp);

        Point[] points = new Point[24];
        for (int i = 0; i < 24; i++) {
            points[i] = new Point(Brick.NONE, 0);
        }
        Board board = new Board(points, players);
        board.setColumn(0, Brick.WHITE, 1);
        board.setColumn(10, Brick.WHITE, 1);
        board.setColumn(14, Brick.WHITE, 5);

        board.setColumn(12, Brick.BLACK, 3);
        board.setColumn(13, Brick.BLACK, 2);
        board.setColumn(17, Brick.BLACK, 1);


        int pipLoss = Evaluation.calculatePipLoss(board, whitep.brick);
        assertEquals(10, pipLoss);
    }
}
