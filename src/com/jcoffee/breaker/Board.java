package com.jcoffee.breaker;

import java.awt.*;
import java.security.SecureRandom;
import java.util.*;

import javax.swing.*;

/**
 * The <code>Board</code> class is the container of the game,
 * it contains all <code>Entities</code> and game logic.
 *
 * @author Adel Khial
 */

public class Board extends JPanel implements Runnable {

    private static final int DELAY = 10000000;
    public static final int MAX_COL = 8;
    private int score = 0;
    private int bricksDestroyed = 0;
    private int frames = 0;
    private boolean showMessage = false;
    private boolean once = true;
    private boolean gameRunning = true;
    private String[] colors = {"blue", "purple", "yellow", "magenta", "green"};
    private SecureRandom random;

    private ArrayList<Entity> bricks = new ArrayList<>();
    private ArrayList<Entity> removeBricks = new ArrayList<>();

    public Board() {
        float h, s, b;
        h = 180f / 360;
        s = 0.6f;
        b = 0.3f;
        setBackground(Color.getHSBColor(h, s, b));
        setDoubleBuffered(true);

        setPreferredSize(new Dimension(487, 600));
        random = new SecureRandom();
        initEntities();

    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public void removeBrick(Entity e) {
        removeBricks.add(e);
    }

    private void initEntities() {
        for(int i = 0; i < MAX_COL + 1; i++) {
            for(int j = 0; j < 10; j++) {
                generate(j, i);
            }
        }
    }

    private void drawEntities(Graphics g) {

        try {
            for(Entity e : bricks) {
                e.draw(g);
            }

            float h, s, b;
            h = System.currentTimeMillis() % 10000;
            h /= 10000;
            s = 100f / 100;
            b = 100f / 100;

            g.setColor(Color.getHSBColor(h, s, b));
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHints(rh);

            g.setFont(new Font("moon", Font.BOLD, 50));
            String message = Integer.toString(score);
            g.drawString(message, 487 / 2 - getFontMetrics(getFont()).stringWidth(message) * 2, 580);

        } catch(ConcurrentModificationException cme) {
            System.out.println("Lots of bullets...");
        }
        if(showMessage) {
            String message = "Press escape to exit";

            g.setFont(new Font("moon", Font.BOLD, 24));

            float h, s, b;
            h = System.currentTimeMillis() % 10000;
            h /= 10000;
            s = 87f / 100;
            b = 100f / 100;
            g.setColor(Color.getHSBColor(h, s, b));

            RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHints(rh);

            g.drawString(message, (getWidth() - getFontMetrics(g.getFont()).stringWidth(message)) / 2, 510);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void cycle() {
        bricks.removeAll(removeBricks);
        removeBricks.clear();

        for(Entity brick : bricks) {
            ((Brick) brick).update(System.nanoTime());
        }

        if(bricksDestroyed < 4) {
            score += bricksDestroyed;
        } else {
            score += bricksDestroyed * bricksDestroyed;
        }
        bricksDestroyed = 0;

        if(score > 1000 && once) {
            notifyWin();
        }

        if(frames % 20 == 0) {
            generateBricks();
        }
        updateFalling();

        ++frames;
    }

    private void notifyWin() {
        Thread messageThread = new Thread(() ->
                JOptionPane.showMessageDialog(this, "Congratulations! You win!", "You win!", JOptionPane.INFORMATION_MESSAGE));
        messageThread.start();
        once = false;
        showMessage = true;
    }

    private Brick getBrick(int x, int y) {
        if(x < 0 || x > 9) {
            return null;
        }
        if(y < 0 || y > MAX_COL) {
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
        Brick brick = new Brick("resources/sprites/" + colorName + "/" + colorName + "_1.png", x * 50, y * 50, colorName);
        bricks.add(brick);
    }

    private void generateBricks() {
        for(int i = 0; i < 10; i++) {
            if(getBrick(i, 0) == null) {
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
        y = brick.getY() / 50;
        b = getBrick(x, y);
        if(checkBricks(b, color)) return true;
        x = brick.getX() / 50;
        y = brick.getY() / 50 + 1;
        b = getBrick(x, y);
        if(checkBricks(b, color)) return true;
        x = brick.getX() / 50;
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
