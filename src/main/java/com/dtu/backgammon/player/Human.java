package com.dtu.backgammon.player;

import java.util.regex.Pattern;

import com.dtu.backgammon.App;
import com.dtu.backgammon.Board.Brick;

public class Human extends Player {

    public Human(Brick brick) {
        super(brick);
    }

    @Override
    public void getMove() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMove'");
    }

    @Override
    public String getName() {
        return "Human";
    }
    
}
