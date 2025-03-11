package com.dtu.backgammon;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.ai.AI;

/**
 * Hello world!
 */
public class App {
    public static Scanner scanner;
    public static void main(String[] args) throws Exception {
        scanner = new Scanner(System.in);
        Board board = new Board();
        //Renderer.render(board);

        /*board.whiteHomeBoard = 15;
        board.setColumn(21, Brick.WHITE, 1);
        board.setColumn(22, Brick.WHITE, 1);*/

        board.barWhite = 1;
        board.setColumn(21, Brick.WHITE, 1);
        board.setColumn(9, Brick.WHITE, 3);
        
        AI ai = (AI) board.players.get(0);

        List<Move[]> actions = ai.actions(board, new LinkedList<>(Arrays.asList( new Integer[] {2,2,2,2} )) );
        for (int i = 0; i < actions.size(); i++) {
            System.out.print("[");
            for (int j = 0; j < actions.get(i).length; j++) {
                System.out.print(actions.get(i)[j].toString() + ", ");
            }
            System.out.println("]");
        }
    }
}
