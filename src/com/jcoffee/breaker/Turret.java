package com.jcoffee.breaker;

import java.awt.*;

/**
 * The class <code>Turret</code> represents te "turret" game object.
 *
 * @author Adel Khial
 */

public class Turret extends Entity {

    private float angle = 270;
    private RenderingHints rh;
    private Sprite[] frames = new Sprite[180];
    private int frameCount = 0;

    public Turret(String ref, double x, double y) {
        super(ref, x, y);
        rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        StringBuilder builder = new StringBuilder();
        builder.append(ref);
        builder.delete(builder.length() - 6, builder.length());
        String spriteRef = builder.toString();

        frames[0] = sprite;

        for(int i = 1; i < 180; i++) {
            frames[i] = SpriteStore.getInstance().getSprite(spriteRef + "_" + i + ".png");
        }
    }

    public void leftRotate() {
        if(angle > 205)
            angle -= 1f;
    }

    public void rightRotate() {
        if(angle < 335)
            angle += 1f;
    }

    public void update() {
        frameCount++;
        sprite = frames[frameCount];
        frameCount %= 179;
    }

    public float getAngle() {
        return angle;
    }

    public void draw(Graphics g) {
        ((Graphics2D) g).setRenderingHints(rh);
        ((Graphics2D) g).rotate(Math.toRadians(angle + 90), x + 39, y + 95);
        g.drawImage(sprite.getImage(), (int) x, (int) y, null);
        ((Graphics2D) g).rotate(-Math.toRadians(angle + 90), x + 39, y + 95);
    }

    @Override
    public void collided(Entity other) {
    } // collision handled elsewhere
}