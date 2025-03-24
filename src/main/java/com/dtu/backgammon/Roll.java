package com.dtu.backgammon;

import java.util.List;

public record Roll(int weight, List<Integer> values) {
    public static final Roll[] ALL_ROLLS;
    public static final int NUM_ROLLS = 6 * 6;

    static {
        ALL_ROLLS = new Roll[21];
        int index = 0;
        for (int i = 1; i <= 6; i++) {
            Roll.ALL_ROLLS[index++] = new Roll(1, List.of(i, i, i, i));
        }
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j < i; j++) {
                Roll.ALL_ROLLS[index++] = new Roll(2, List.of(i, j));
            }
        }
    }
}
