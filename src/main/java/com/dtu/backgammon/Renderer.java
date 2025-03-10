package com.dtu.backgammon;

import java.util.List;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.player.Player;

public class Renderer {
    // See ANSI commands https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
    public static void render(Board board) {

        
        //setBackgroundColor();
        clearScreen(false);
        printBoard(board);
        printPlayers(board.players);
    }

    static String graybar_color = "40;40;40";
    static String lighttick_color = "125;106;54";
    static String darktick_color = "69;55;15";
    private static void printBoard(Board board) {
        moveCur(0,0);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                if (j == 6) { System.out.print(bcol(graybar_color) + "   "); }
                Brick brick = board.getBrickAt(j, i);
                System.out.print(bcol(j%2 == 0 ? lighttick_color : darktick_color) + getBrickPrintStr(brick));
            }
            System.out.println();
        }

        moveCur(0,6);
        // Print gray bar in the middle of green
        for (int i = 6; i < 9; i++) {
            System.out.print(bcol(backgroundcolor) + "   ".repeat(6));
            System.out.print(bcol(graybar_color) + "   ");
            System.out.println(bcol(backgroundcolor) + "   ".repeat(6));
        }

        moveCur(0,6+3);
        for (int i = 4; i >= 0; i--) {
            for (int j = 12; j < 24; j++) {
                if (j == 18) { System.out.print(bcol(graybar_color) + "   "); }
                Brick brick = board.getBrickAt(j, i);
                System.out.print(bcol(j%2 == 1 ? lighttick_color : darktick_color) + getBrickPrintStr(brick));
            }
            System.out.println();
        }
        resetColor();
        // Temporary move cursor below board
        moveCur(0, 20);
    }

    private static void printPlayers(List<Player> players) {
        System.out.println();
        for (int i = 0; i < players.size(); i++) {
            System.out.print((i>0 ? " Vs. " : "") + players.get(i).getName());
        }
    }

    static String white_color = "255;255;255";
    static String black_color = "0;0;0";
    private static String getBrickPrintStr(Brick brick) {
        switch (brick) {
            case NONE:
                return "   ";
            case WHITE:
                return fcol(white_color) + " \u25CF ";
            case BLACK:
                return fcol(black_color) + " \u25CF ";
            default:
                return "";
        }
    }
        
    static String backgroundcolor = "17;125;7";
    private static void setBackgroundColor() {
        System.out.println(esc + "[m" + bcol(backgroundcolor));
    }
    private static void clearScreen(boolean keepColor) {
        System.out.println((keepColor ? (esc + "[0m") : "" ) + esc + "[2J");
    }
    private static String bcol(String rgbcol) {
        return esc + "[48;2;" + rgbcol + "m";
    }
    private static String fcol(String rgbcol) {
        return esc + "[38;2;" + rgbcol + "m";
    }
    private static void resetColor() {
        System.out.println(esc + "[0m");
    }
    private static void moveCur(int x, int y) {
        System.out.print(esc + String.format("[%d;%dH", y, x));
    }


    private static String esc = "\u001b";
}
