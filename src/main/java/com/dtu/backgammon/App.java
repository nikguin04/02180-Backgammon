package com.dtu.backgammon;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static Scanner scanner;
    public static void main(String[] args) throws Exception {
        scanner = new Scanner(System.in);
        Board board = new Board();
        Renderer.render(board);

    }
}
