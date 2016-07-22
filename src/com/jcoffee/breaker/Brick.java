package com.jcoffee.breaker;

/**
 * The class <code>Brick</code> represents all breakable game objects.
 *
 * @author Adel Khial
 */

public class Brick extends Entity {

    private Board board;

    public Brick(String ref, double x, double y, Board board) {
        super(ref, x, y);
        this.board = board;
    }

    @Override
    public void collided(Entity other) {
    } // collision handled elsewhere
}
