package com.jcoffee.breaker;

/**
 * The class <code>Brick</code> represents all breakable game objects.
 *
 * @author Adel Khial
 */

public class Brick extends Entity {

    private Sprite[] frames = new Sprite[5];
    private int frameCount = 0;
    private long lastFrame;

    public Brick(String ref, double x, double y) {

        super(ref, x, y);

        StringBuilder builder = new StringBuilder();
        builder.append(ref);
        builder.delete(builder.length() - 6, builder.length());
        String spriteRef = builder.toString();

        frames[0] = sprite;
        frames[1] = SpriteStore.getInstance().getSprite(spriteRef + "_2.png");
        frames[2] = SpriteStore.getInstance().getSprite(spriteRef + "_3.png");
        frames[3] = SpriteStore.getInstance().getSprite(spriteRef + "_4.png");
        frames[4] = SpriteStore.getInstance().getSprite(spriteRef + "_5.png");

    }

    public void update(long time) {
        int frameLength = 100000000;
        if(time - lastFrame > frameLength) {
            sprite = frames[frameCount];
            frameCount++;
            frameCount %= 5;
            lastFrame = time;
        }
    }

    @Override
    public void collided(Entity other) {
    } // collision handled elsewhere
}
