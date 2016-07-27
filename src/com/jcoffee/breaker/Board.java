package com.jcoffee.breaker;

import java.awt.*;
import java.security.SecureRandom;
import java.util.*;

import javax.swing.*;
import java.util.Timer;

/**
 * The <code>Board</code> class is the container of the game,
 * it contains all <code>Entities</code> and game logic.
 *
 * @author Adel Khial
 */

public class Board extends JPanel implements Runnable {

    private static final int DELAY = 10000000;
    private long lastFireTime = 0;
    private int brickCount = 80;
    private boolean showMessage = false;
    private boolean gameRunning = true;
    private boolean rightPressed = false;
    private boolean leftPressed = false;
    private boolean firePressed = false;
    private Turret turret;
    private Sound shotSound;
    private Sound music;
    private String[] colors = {"blue", "purple", "yellow", "magenta", "green"};
    private SecureRandom random;

    private ArrayList<Entity> bullets = new ArrayList<>();
    private ArrayList<Entity> bricks = new ArrayList<>();
    private ArrayList<Entity> removeBullets = new ArrayList<>();
    private ArrayList<Entity> removeBricks = new ArrayList<>();

    public Board() {
        float h, s, b;
        h = 180f / 360;
        s = 0.6f;
        b = 0.4f;
        setBackground(Color.getHSBColor(h, s, b));
        setDoubleBuffered(true);

        setPreferredSize(new Dimension(487, 640));
        random = new SecureRandom();
        initEntities();

        shotSound = new Sound("resources/sounds/bullet_shot.wav");
        shotSound.setShot(true);
        shotSound.setVolume(-13.0f);

        music = new Sound("resources/sounds/music.wav");
        music.setLoop(true);
        music.play();

    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public void setFirePressed(boolean firePressed) {
        this.firePressed = firePressed;
    }

    public void removeBrick(Entity e) {
        removeBricks.add(e);
        --brickCount;
    }

    public void removeBullet(Entity e) {
        removeBullets.add(e);
    }

    public int getAngle() {
        return turret.getAngle();
    }

    private void initEntities() {
        turret = new Turret("resources/sprites/turret/turret_0.png", 487 / 2 - 34, 640 - 140);
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 10; j++) {
                int color = random.nextInt(colors.length);
                String colorName = colors[color];
                Brick brick = new Brick("resources/sprites/" + colorName + "/" + colorName + "_1.png", j * 50, i * 50, colorName);
                bricks.add(brick);
            }
        }
    }

    private void fire() {
        int fireSpeed = 100000000;
        long curTime = System.nanoTime();
        if(curTime - lastFireTime > fireSpeed) {
            lastFireTime = curTime;
            // 229, 430
            shotSound.play();
            Bullet bullet = new Bullet("resources/sprites/bullet.png",
                    turret.getX() + 20 + 125 * Math.sin(Math.toRadians(-turret.getAngle() + 90)),
                    turret.getY() + 75 + 125 * Math.cos(Math.toRadians(-turret.getAngle() + 90)), this);
            bullets.add(bullet);

        }
    }

    private void notifyWin() {
        Thread messageThread = new Thread(() ->
                JOptionPane.showMessageDialog(this, "Congratulations! You win!", "You win!", JOptionPane.INFORMATION_MESSAGE));
        messageThread.start();
        brickCount = -1;
        firePressed = false;
        showMessage = true;
        // music.stop();
        music.setVolume(-22.0f);
        Sound end = new Sound("resources/sounds/end_game.wav");
        end.setVolume(6.0f);
        end.play();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                music.setVolume(0.0f);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 8000);
    }

    private Brick getBrick(int x, int y) {
        if(x < 0 || x > 10) {
            return null;
        }
        if(y < 0 || y > 8) {
            return null;
        }
        for(Entity b : bricks) {
            if(b.getX() == x * 50 && b.getY() == y * 50) {
                return (Brick) b;
            }
        }
        return null;
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

        removeBrick(brick);

        return false;
    }

    private void drawEntities(Graphics g) {

        try {
            turret.draw(g);
            for(Entity e : bullets)
                e.draw(g);
            for(Entity e : bricks)
                e.draw(g);
        } catch(ConcurrentModificationException cme) {
            System.out.println("Lots of bullets...");
        }
        if(showMessage) {
            String message = "Press escape to exit...";

            g.setFont(new Font("moon", Font.BOLD, 24));

            float h, s, b;
            h = System.currentTimeMillis() % 10000;
            h /= 10000;
            s = 87f / 100;
            b = 100f / 100;
            g.setColor(Color.getHSBColor(h, s, b));

            RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHints(rh);

            g.drawString(message, (getWidth() - getFontMetrics(g.getFont()).stringWidth(message)) / 2, getHeight() / 2);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void cycle() {
        bricks.removeAll(removeBricks);
        removeBricks.clear();
        bullets.removeAll(removeBullets);
        removeBullets.clear();

        for(Entity brick : bricks) {
            ((Brick) brick).update(System.nanoTime());
        }
        turret.update();

        if(leftPressed && !rightPressed) {
            turret.leftRotate();
        } else if(rightPressed && !leftPressed) {
            turret.rightRotate();
        }

        if(firePressed) {
            fire();
        }

        for(Entity bullet : bullets) {
            bricks.forEach((Entity brick) -> {
                if(bullet.isCollided(brick))
                    bullet.collided(brick);
            });
        }

        if(brickCount == 0) {
            notifyWin();
        }

        for(Entity e : bullets) {
            e.move(DELAY);
        }
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
        lastFireTime = before;

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
