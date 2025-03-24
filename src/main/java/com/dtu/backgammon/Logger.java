package com.dtu.backgammon;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Logger {
    private static Writer logWriter;
    private static Writer evalWriter;

    public static void init(String path) throws IOException {
        logWriter = new FileWriter(path);
    }

    public static void initEval(String path) throws IOException {
        evalWriter = new FileWriter(path);
        evalWriter.write("brick,blot,piploss,homeboard,blockades,wintray,stacking\n");
    }

    public static void write(String str) {
        try {
            logWriter.write(str);
        } catch (IOException ignored) {}
    }

    public static void eval(Board.Brick brick, int blothits, int piploss, int homeboard, int blockades, int wintray, int stacking) {
        if (evalWriter == null) { return; }
        try {
            evalWriter.write(String.format("%s,%d,%d,%d,%d,%d,%d\n", brick.name(), blothits, piploss, homeboard, blockades, wintray, stacking));
        } catch (IOException ignored) {}
    }

    public static void close() throws IOException {
        logWriter.close();
        if (evalWriter != null) { evalWriter.close(); }
    }
}
