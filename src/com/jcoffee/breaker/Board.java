package com.jcoffee.breaker;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * The <code>Board</code> class is the container of the game,
 * it contains all <code>Entities</code> and game logic.
 *
 * @author Adel Khial
 */

public class Board extends JPanel implements Runnable {

    public static final int DELAY = 10000000;
    public static final int MAX_COL = 8;
    private boolean gameRunning = true;
    private int bricksDestroyed = 0;
    private int frames = 0;
    private int lastScore;
    private int score = 0;
    private Image image;
    private SecureRandom random;
    private Sound one, two, three, four;
    private String[] colors = {"diamond", "jade", "pink", "purple", "turquoise", "ruby"};

    private ArrayList<Entity> bricks = new ArrayList<>();
    private ArrayList<Entity> bursts = new ArrayList<>();
    private ArrayList<Entity> removeBricks = new ArrayList<>();
    private ArrayList<Entity> removeBursts = new ArrayList<>();

    public Board() {
        setDoubleBuffered(true);

        float h, s, b;
        h = 277.14f / 360;
        s = 16.47f / 100;
        b = 1;
        setBackground(Color.getHSBColor(h, s, b));
        setPreferredSize(new Dimension(488, 600));

        initSounds();
        initEntities();

    }

    private void initSounds() {
        one = new Sound("resources/sounds/one.wav");
        two = new Sound("resources/sounds/two.wav");
        three = new Sound("resources/sounds/three.wav");
        four = new Sound("resources/sounds/four.wav");
    }

    private void initEntities() {
        for(int i = 0; i < 7; i++) {
            // this is to cache "burst" sprites in the instantiation stage
            Burst burst = new Burst("resources/bursts/" + (i + 1) + "/0.png", 0, 0, this);
            burst.getX(); // to avoid unused variable warning
        }

        random = new SecureRandom();

        for(int i = 1; i < MAX_COL + 1; i++) {
            for(int j = 1; j < 9; j++) {
                generate(j, i);
            }
        }

        URL url = getClass().getClassLoader().getResource("resources/backdrop.png");
        try {
            if(url != null) {
                image = ImageIO.read(url);
            }
        } catch(IOException e) {
            System.err.println("Unable to load backdrop");
        }
    }

    public void stopGame() {
        gameRunning = false;
    }

    public void removeBrick(Entity e) {
        int num = random.nextInt(7);

        Burst burst = new Burst("resources/bursts/" + (num + 1) + "/0.png", e.getX() - 6, e.getY() - 6, this);
        bursts.add(burst);
        removeBricks.add(e);
    }

    public void removeBurst(Entity e) {
        removeBursts.add(e);
    }

    private void drawScore(Graphics g) {
        float h, s, b;

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHints(rh);
        g.setFont(new Font("Yanone Kaffeesatz", Font.BOLD, 72));
        String message = Integer.toString(score);

        h = 200f / 360;
        s = 100f / 100;
        b = 75f / 100;
        g.setColor(Color.getHSBColor(h, s, b));
        g.drawString(message, 480 / 2 - getFontMetrics(getFont()).stringWidth(message) * 2 + 2, 580 + 2);

        h = 200f / 360;
        s = 100f / 100;
        b = 100f / 100;
        g.setColor(Color.getHSBColor(h, s, b));
        g.drawString(message, 480 / 2 - getFontMetrics(getFont()).stringWidth(message) * 2, 580);

        message = "+" + lastScore;
        g.setFont(new Font("Yanone Kaffeesatz", Font.BOLD, 48));
        g.drawString(message, 380, 580);
    }

    private void drawEntities(Graphics g) {

        try {
            for(Entity e : bricks) {
                e.draw(g);
            }
            for(Entity e : bursts) {
                e.draw(g);
            }
            drawScore(g);

        } catch(ConcurrentModificationException cme) {
            System.out.println("Lots of bullets...");
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void cycle() {
        bricks.removeAll(removeBricks);
        removeBricks.clear();
        bursts.removeAll(removeBursts);
        removeBursts.clear();

        for(Entity brick : bricks) {
            ((Animatable) brick).update(System.nanoTime());
        }
        for(Entity burst : bursts) {
            ((Animatable) burst).update(System.nanoTime());
        }
        if(bricksDestroyed != 0) {
            if(bricksDestroyed < 4) {
                lastScore = bricksDestroyed;
                score += lastScore;
            } else {
                lastScore = bricksDestroyed * bricksDestroyed;
                score += lastScore;
            }
            if(bricksDestroyed < 4) {
                one.play();
            } else if(bricksDestroyed < 6) {
                two.play();
            } else if(bricksDestroyed < 9) {
                three.play();
            } else {
                four.play();
            }
        }
        bricksDestroyed = 0;

        if(frames % 20 == 0) {
            generateTopBricks();
        }
        updateFalling();

        ++frames;
    }

    private Brick getBrick(int x, int y) {
        if(x < 1 || x > 8) {
            return null;
        }
        if(y < 1 || y > MAX_COL) {
            return null;
        }
        for(Entity b : bricks) {
            if(b.getX() == x * 50 && b.getY() == y * 50) {
                return (Brick) b;
            }
        }
        return null;
    }

    private void generate(int x, int y) {
        int color = random.nextInt(5);
        String colorName = colors[color];
        Brick brick = new Brick("resources/sprites/" + colorName + ".png", x * 50, y * 50, colorName);
        bricks.add(brick);
    }

    private void generateTopBricks() {
        for(int i = 1; i < 9; i++) {
            if(getBrick(i, 1) == null) {
                generate(i, -1);
            }
        }
    }

    private void updateFalling() {
        for(Entity brick : bricks) {
            int x = brick.getX() / 50;
            int y = brick.getY() / 50;

            if((getBrick(x, y + 1) == null && y != MAX_COL) || brick.getY() % 50 != 0) {
                ((Brick) brick).fall();
            }
        }
    }

    public void checkBricks(int x, int y) {
        x /= 50;
        y /= 50;

        Brick brick = getBrick(x, y);
        if(brick != null) {
            checkBricks(brick, brick.getColor());
        }
    }

    public boolean checkBricks(Brick brick, String color) {
        int x, y;
        if(brick == null) {
            return false;
        }
        if(brick.isVisited() || !brick.getColor().equals(color)) {
            return false;
        }
        brick.setVisited();
        Brick b;

        x = brick.getX() / 50 + 1;
        y = brick.getY() / 50;
        b = getBrick(x, y);
        if(checkBricks(b, color)) return true;
        x = brick.getX() / 50 - 1;
        b = getBrick(x, y);
        if(checkBricks(b, color)) return true;
        x = brick.getX() / 50;
        y = brick.getY() / 50 + 1;
        b = getBrick(x, y);
        if(checkBricks(b, color)) return true;
        y = brick.getY() / 50 - 1;
        b = getBrick(x, y);
        if(checkBricks(b, color)) return true;

        ++bricksDestroyed;
        removeBrick(brick);

        return false;
    }

    @Override
    public void addNotify() {
        super.addNotify();

        Thread animator = new Thread(this);
        animator.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 6, 0, null); // to correctly center image
        drawEntities(g);
    }

    @Override
    public void run() {
        long before, diff, sleep;

        before = System.nanoTime();

        while(gameRunning) {
            cycle();
            repaint();

            diff = System.nanoTime() - before;
            sleep = DELAY - diff;

            if(sleep < 0)
                sleep = 2000000;

            try {
                Thread.sleep(sleep / 1000000, (int) sleep % 1000000);
            } catch(InterruptedException e) {
                System.err.println("Interrupted " + e.getMessage());
            }

            before = System.nanoTime();
        }
        System.exit(0);
    }
}
