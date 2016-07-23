package com.jcoffee.breaker;

/**
 * The class <code>Brick</code> represents all breakable game objects.
 *
 * @author Adel Khial
 */

public class Brick extends Entity {

    private Board board;
    private String color;

    public Brick(String ref, double x, double y, Board board, String color) {
        super(ref, x, y);
        this.board = board;
        this.color = color;
    }

    @Override
    public void collided(Entity other) {
    } // collision handled elsewhere
}
