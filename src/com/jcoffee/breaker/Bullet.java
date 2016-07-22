package com.jcoffee.breaker;

import java.awt.*;

/**
 * The class <code>Bullet</code> represents in-game projectiles.
 *
 * @author Adel Khial
 */

public class Bullet extends Entity {

    private Board board;
    private int angle;

    public Bullet(String ref, double x, double y, Board board) {
        super(ref, x, y);
        this.board = board;
        angle = board.getAngle();
    }

    public void move(long delay) {
        if(y < -40) {
            board.removeBullet(this);
        } else {
            dx = Math.cos(Math.toRadians(angle)) * 1000;
            dy = Math.sin(Math.toRadians(angle)) * 1000;
            super.move(delay);
        }
    }

    public void draw(Graphics g) {
        ((Graphics2D) g).rotate(Math.toRadians(angle + 90), x + 20, y + 20);
        g.drawImage(sprite.getImage(), (int) x, (int) y, null);
        ((Graphics2D) g).rotate(-Math.toRadians(angle + 90), x + 20, y + 20);
    }

    @Override
    public void collided(Entity other) {
        board.removeBullet(this);
        board.removeBrick(other);
        board.notifyBrick();
    }
}
