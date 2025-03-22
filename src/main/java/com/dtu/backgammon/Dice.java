package com.dtu.backgammon;

import java.util.Random;

public class Dice {
    private int eyesDye1;
    private int eyesDye2;
    private Random random;

    public Dice() {
        random = new Random();
    }

    public int[] rollDice() {
        eyesDye1 = random.nextInt(6) + 1;
        eyesDye2 = random.nextInt(6) + 1;
        return getMoves();
    }

    public int[] getMoves() {
        if (eyesDye1 == eyesDye2) {
            return new int[]{eyesDye1, eyesDye1, eyesDye1, eyesDye1};
        } else {
            return new int[]{eyesDye1, eyesDye2};
        }
    }

    public int getTotalMoveValue() {
        int count = 0;
        for (int move : this.getMoves()) {
            count += move;
        }
        return count;
    }

    public void displayDices() {
        System.out.println("Dice Roll: " + eyesDye1 + " & " + eyesDye2);
    }
}
