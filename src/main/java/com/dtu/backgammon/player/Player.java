package com.dtu.backgammon.player;

import com.dtu.backgammon.Board.Brick;

public abstract class Player {
    Brick brick;
    public Player(Brick brick) {
        this.brick = brick;
    }
    
    public abstract void Move();
    public abstract String getName();
}