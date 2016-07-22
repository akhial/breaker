package com.jcoffee.breaker;

import java.awt.Graphics;
import java.awt.Image;

/**
 * The class <code>Sprite</code> represents a single 2D sprite.
 *
 * @author Kevin Glass
 */

public class Sprite {

    private Image image;

    public Sprite(Image image) {
        this.image = image;
    }

    public int getWidth() {
        return image.getWidth(null);
    }

    public int getHeight() {
        return image.getHeight(null);
    }

    public Image getImage() {
        return image;
    }

    public void draw(Graphics g, int x, int y) {
        g.drawImage(image, x, y, null);
    }

}
