package src.main.java.com.dtu.backgammon;

public class Renderer {
    // See ANSI commands https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
    public static void render() {
        setBackgroundColor();
        clearScreen();
        printBoard();
    }

    static String graybar_color = "40;40;40";
    static String lighttick_color = "125;106;54";
    static String darktick_color = "69;55;15";
    private static void printBoard() {
        moveCur(0,0);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 12; j++) {
                if (j == 6) { System.out.print(bcol(graybar_color) + "   "); }
                System.out.print(bcol(j%2 == 0 ? lighttick_color : darktick_color) + "   ");
            }
            System.err.println();
        }
        moveCur(0,6+3);
        for (int i = 12; i > 6; i--) {
            for (int j = 0; j < 12; j++) {
                if (j == 6) { System.out.print(bcol(graybar_color) + "   "); }
                System.out.print(bcol(j%2 == 1 ? lighttick_color : darktick_color) + "   ");
            }
            System.err.println();
        }

        // Print gray bar in the middle of green
        for (int i = 6; i < 10; i++) {
            moveCur(6*3+1,i);
            System.out.print(bcol(graybar_color) + "   ");
        }

        // Temporary move cursor below board
        moveCur(0, 20);
    }
        
    static String backgroundcolor = "17;125;7";
    private static void setBackgroundColor() {
        System.out.println(esc + "[m" + bcol(backgroundcolor));
    }
    private static void clearScreen() {
        System.out.println(esc + "[2J");
    }
    private static String bcol(String rgbcol) {
        return esc + "[48;2;" + rgbcol + "m";
    }
    private static String fcol(String rgbcol) {
        return esc + "[38;2;" + rgbcol + "m";
    }
    private static void moveCur(int x, int y) {
        System.out.print(esc + String.format("[%d;%dH", y, x));
    }


    private static String esc = "\u001b";
}
