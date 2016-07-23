package com.jcoffee.breaker;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The abstract class <code>Entity</code> represents a single drawable
 * Entity on a game <code>Board</code>, it is the superclass of all classes
 * that represent game objects.
 *
 * @author Adel Khial
 */

public abstract class Entity {

    private static final int SEC = 1000000000;


    protected double x;
    protected double y;
    protected double dx;
    protected double dy;

    protected Sprite sprite;
    private Rectangle me = new Rectangle();
    private Rectangle him = new Rectangle();

    public Entity(String ref, double x, double y) {
        sprite = SpriteStore.getInstance().getSprite(ref);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    private int getHeight() {
        return sprite.getHeight();
    }

    private int getWidth() {
        return sprite.getWidth();
    }

    public void move(long delay) {
        x += (dx * delay) / SEC;
        y += (dy * delay) / SEC;
    }

    public void draw(Graphics g) {
        sprite.draw(g, (int) x, (int) y);
    }

    public boolean isCollided(Entity other) {
        me.setBounds((int) x, (int) y, getWidth() - 10, getHeight() - 10);
        him.setBounds((int) other.x, (int) other.y, other.getWidth(), other.getHeight());

        return me.intersects(him);
    }

    public abstract void collided(Entity other);

}
