package com.jcoffee.breaker;

/**
 * The class <code>Brick</code> represents all breakable game objects.
 *
 * @author Adel Khial
 */

public class Brick extends Entity implements Animatable {

    private Sprite[] frames;
    private int frameCount = 0;
    private long lastFrame;
    private String color;
    private boolean visited;

    public Brick(String ref, double x, double y, String color) {

        super(ref, x, y);

        frames = new Sprite[1];
        frames[0] = sprite;

        this.color = color;
        visited = false;

    }

    public String getColor() {
        return color;
    }

    public void setVisited() {
        visited = true;
    }

    public boolean isVisited() {
        return visited;
    }

    public void fall() {
        y += 5;
    }

    @Override
    public void update(long time) {
        int frameLength = 80000000;
        if(time - lastFrame > frameLength) {
            sprite = frames[frameCount];
            frameCount++;
            frameCount %= frames.length;
            lastFrame = time;
        }
    }
}
