package com.jcoffee.breaker;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * The class <code>Turret</code> represents te "turret" game object.
 *
 * @author Adel Khial
 */

public class Turret extends Entity {

    private int angle = 270;
    private RenderingHints rh;

    public Turret(String ref, double x, double y) {
        super(ref, x, y);
        rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public void leftRotate() {
        if(angle > 205)
            angle -= 1;
    }

    public void rightRotate() {
        if(angle < 335)
            angle += 1;
    }

    public int getAngle() {
        return angle;
    }

    public void draw(Graphics g) {
        ((Graphics2D) g).setRenderingHints(rh);
        ((Graphics2D) g).rotate(Math.toRadians(angle + 90), x + 50 - 10, y + 80);
        g.drawImage(sprite.getImage(), (int) x, (int) y, null);
        ((Graphics2D) g).rotate(-Math.toRadians(angle + 90), x + 50 - 10, y + 80);
    }

    @Override
    public void collided(Entity other) {
    } // collision handled elsewhere
}