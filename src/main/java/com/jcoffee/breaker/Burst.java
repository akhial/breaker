package com.jcoffee.breaker;

/**
 * The class <code>Burst</code> is a colorful explosion spawned when a brick
 * is destroyed.
 *
 * @author Adel Khial
 */

public class Burst extends Entity implements Animatable {

    private final Board board;
    private int frameCount = 0;
    private long lastFrame;
    private final Sprite[] frames;

    public Burst(String ref, double x, double y, Board board) {
        super(ref, x, y);
        this.board = board;

        frames = new Sprite[82];
        frames[0] = sprite;

        StringBuilder builder = new StringBuilder();
        builder.append(ref);
        builder.delete(builder.length() - 5, builder.length());
        String spriteRef = builder.toString();

        for(int i = 1; i < 82; i++) {
            frames[i] = SpriteStore.getInstance().getSprite(spriteRef + i + ".png");
        }
    }

    @Override
    public void update(long time) {
        int frameLength = 10000000;
        if(time - lastFrame > frameLength) {
            sprite = frames[frameCount];
            frameCount++;
            if(frameCount == frames.length) {
                frameCount = 0;
                board.removeBurst(this);
            }
            lastFrame = time;
        }
    }
}
