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
        for (int j = 11; j >= 0; j--) { // Print indexes for column
            if (j == 5) { System.out.print("   "); } // Pad grey bar
            System.out.print(String.format("%3d", j));
        }

        System.out.println();
        for (int i = 0; i < 5; i++) {
            for (int j = 11; j >= 0; j--) {
                if (j == 5) { // Print the white bar
                    boolean brickInBar = board.barWhite > i;
                    System.out.print(bcol(graybar_color) + (brickInBar ? fcol(white_color) + " \u25CF " : "   "));
                    resetColor();
                }
                Brick brick = board.getBrickAt(j, i);
                System.out.print(bcol(j%2 == 1 ? lighttick_color : darktick_color) + getBrickPrintStr(brick));
            }
            System.out.println();
        }

        moveCur(0,6+1);
        // Print gray bar in the middle of green
        for (int i = 6; i < 9; i++) {
            System.out.print(bcol(backgroundcolor) + "   ".repeat(6));
            System.out.print(bcol(graybar_color) + "   ");
            System.out.println(bcol(backgroundcolor) + "   ".repeat(6));
        }

        moveCur(0,6+3+1);
        for (int i = 4; i >= 0; i--) {
            for (int j = 12; j < 24; j++) {
                if (j == 18) { // Print the black bar
                    boolean brickInBar = board.barBlack > i;
                    System.out.print(bcol(graybar_color) + (brickInBar ? fcol(black_color) + " \u25CF " : "   "));
                    resetColor();
                }
                Brick brick = board.getBrickAt(j, i);
                System.out.print(bcol(j%2 == 1 ? lighttick_color : darktick_color) + getBrickPrintStr(brick));
            }
            System.out.println();
        }
        resetColor();
        for (int j = 12; j < 24; j++) { // Print indexes for column
            if (j == 18) { System.out.print("   "); } // Pad grey bar
            System.out.print(String.format("%3d", j));
        }
        // Temporary move cursor below board
        moveCur(0, 16);
    }

    private static void printPlayers(List<Player> players) {
        System.out.println();
        for (int i = 0; i < players.size(); i++) {
            System.out.print((i>0 ? " Vs. " : "") + players.get(i).getName());
        }
        System.out.println();
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
        System.out.print(esc + "[m" + bcol(backgroundcolor));
    }
    private static void clearScreen(boolean keepColor) {
        System.out.print((keepColor ? (esc + "[0m") : "" ) + esc + "[2J");
    }
    private static String bcol(String rgbcol) {
        return esc + "[48;2;" + rgbcol + "m";
    }
    private static String fcol(String rgbcol) {
        return esc + "[38;2;" + rgbcol + "m";
    }
    private static void resetColor() {
        System.out.print(esc + "[0m");
    }
    private static void moveCur(int x, int y) {
        System.out.print(esc + String.format("[%d;%dH", y, x));
    }


    private static String esc = "\u001b";
}
