package com.dtu.backgammon;

import java.util.List;

import com.dtu.backgammon.Board.Brick;
import com.dtu.backgammon.player.Player;

public class Renderer {
    // See ANSI commands https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
    public static void render(Board board) {
        showCursor(false);
        printBoard(board);
        System.out.print(ESC + "[J");
        showCursor(true);
    }

    static final String barColor = "64;64;64";
    static final String lightTickColor = "125;106;54";
    static final String darkTickColor = "69;55;15";
    private static void printBoard(Board board) {
        moveCur(0, 0);
        for (int i = 11; i >= 0; i--) { // Print indexes for column
            if (i == 5) { System.out.print("   "); } // Pad grey bar
            System.out.printf("%3d", i);
        }

        System.out.println();
        for (int row = 0; row < 5; row++) {
            for (int point = 11; point >= 0; point--) {
                if (point == 5) { // Print the bar for white
                    boolean brickInBar = board.barWhite > row;
                    System.out.print(bcol(barColor) + (brickInBar ? fcol(whiteColor) + " \u25CF " : "   "));
                    resetColor();
                }
                Brick brick = board.getBrickAt(point, row);
                System.out.print(bcol(point % 2 == 1 ? lightTickColor : darkTickColor) + getBrickPrintStr(brick));
            }
            resetColor();
            System.out.println();
        }

        moveCur(0, 6 + 1);
        // Print grey bar in the middle of green
        for (int row = 6; row < 9; row++) {
            System.out.print(bcol(backgroundColor) + "   ".repeat(6));
            System.out.print(bcol(barColor) + "   ");
            System.out.print(bcol(backgroundColor) + "   ".repeat(6));
            resetColor();
            System.out.println();
        }

        moveCur(0, 6 + 3 + 1);
        for (int row = 4; row >= 0; row--) {
            for (int point = 12; point < 24; point++) {
                if (point == 18) { // Print the bar for black
                    boolean brickInBar = board.barBlack > row;
                    System.out.print(bcol(barColor) + (brickInBar ? fcol(blackColor) + " \u25CF " : "   "));
                    resetColor();
                }
                Brick brick = board.getBrickAt(point, row);
                System.out.print(bcol(point % 2 == 1 ? lightTickColor : darkTickColor) + getBrickPrintStr(brick));
            }
            resetColor();
            System.out.println();
        }
        resetColor();
        for (int i = 12; i < 24; i++) { // Print indexes for column
            if (i == 18) { System.out.print("   "); } // Pad grey bar
            System.out.printf("%3d", i);
        }
        // Move cursor below board
        moveCur(0, 16);
        System.out.println();
    }

    static String whiteColor = "255;255;255";
    static String blackColor = "0;0;0";
    private static String getBrickPrintStr(Brick brick) {
        return switch (brick) {
            case NONE -> "   ";
            case WHITE -> fcol(whiteColor) + " \u25CF ";
            case BLACK -> fcol(blackColor) + " \u25CF ";
        };
    }

    static String backgroundColor = "17;125;7";
    private static void setBackgroundColor() {
        System.out.print(ESC + "[m" + bcol(backgroundColor));
    }
    public static void clearScreen() {
        System.out.print(ESC + "[2J");
    }
    private static String bcol(String rgbcol) {
        return ESC + "[48;2;" + rgbcol + "m";
    }
    private static String fcol(String rgbcol) {
        return ESC + "[38;2;" + rgbcol + "m";
    }
    private static void resetColor() {
        System.out.print(ESC + "[m");
    }
    private static void moveCur(int x, int y) {
        System.out.print(ESC + String.format("[%d;%dH", y, x));
    }
    private static void showCursor(boolean show) {
        System.out.print(ESC + "[?25" + (show ? "h" : "l"));
    }

    private static final char ESC = '\u001B';
}
