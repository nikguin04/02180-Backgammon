package com.dtu.backgammon;

import java.util.Random;

public class Dice {
    private int eyesDye1;
    private int eyesDye2;
    private Random random;

    public Dice() {
        random = new Random();
    }

    public Dice(int eye1, int eye2) {
        eyesDye1 = eye1;
        eyesDye2 = eye2;
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

    public int[] getPossibleProgression() {
        if (eyesDye1 == eyesDye2) {
            return new int[]{eyesDye1, eyesDye1*2, eyesDye1*3, eyesDye1*4};
        } else {
            return new int[]{eyesDye1, eyesDye2, eyesDye1+eyesDye2};
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
