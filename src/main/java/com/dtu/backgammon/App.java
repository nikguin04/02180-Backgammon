package com.dtu.backgammon;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        Board board = new Board();
        Renderer.render(board);

    }
}
