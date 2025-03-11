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

/**
 * Hello world!
 */
public class App {
    public static Scanner scanner;
    public static void main(String[] args) throws Exception {
        scanner = new Scanner(System.in);
        Board board = new Board();
        Renderer.render(board);


        /*List<Move[]> actions = board.actions(Brick.BLACK, new LinkedList<>(Arrays.asList( new Integer[] {1,3} )) );
        for (int i = 0; i < actions.size(); i++) {
            System.out.print("[");
            for (int j = 0; j < actions.get(i).length; j++) {
                System.out.print(actions.get(i)[j].toString() + ", ");
            }
            System.out.println("]");
        }*/
    }
}
