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

    protected double x;
    protected double y;

    protected Sprite sprite;

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

    public void draw(Graphics g) {
        sprite.draw(g, (int) x, (int) y);
    }

}
