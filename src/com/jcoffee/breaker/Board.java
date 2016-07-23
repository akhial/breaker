package com.jcoffee.breaker;

import java.awt.*;
import java.security.SecureRandom;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

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
        s = 0.4f;
        b = 0.4f;
        setBackground(Color.getHSBColor(h, s, b));
        setDoubleBuffered(false);

        setPreferredSize(new Dimension(487, 640));
        random = new SecureRandom();
        initEntities();

        shotSound = new Sound("resources/bullet_shot.wav");
        shotSound.setShot(true);
        shotSound.setVolume(-13.0f);

        music = new Sound("resources/music.wav");
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
    }

    public void removeBullet(Entity e) {
        removeBullets.add(e);
    }

    public void notifyBrick() {
        --brickCount;
    }

    public int getAngle() {
        return turret.getAngle();
    }

    private void initEntities() {
        turret = new Turret("resources/turret.png", 487 / 2 - 34, 640 - 160);
        for(int i = 0; i < brickCount / 10; i++) {
            for(int j = 0; j < 10; j++) {
                int color = random.nextInt(5);
                Brick brick = new Brick("resources/" + colors[color] + "/" + colors[color] + "_1.png", j * 50, i * 50, this, colors[color]);
                bricks.add(brick);
            }
        }
    }

    private void fire() {
        int fireSpeed = 200000000;
        long curTime = System.nanoTime();
        if(curTime - lastFireTime > fireSpeed) {
            lastFireTime = curTime;
            // 229, 430
            shotSound.play();
            Bullet bullet = new Bullet("resources/bullet.png",
                    turret.getX() + 20 + 110 * Math.sin(Math.toRadians(-turret.getAngle() + 90)),
                    turret.getY() + 60 + 110 * Math.cos(Math.toRadians(-turret.getAngle() + 90)), this);
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
        Sound end = new Sound("resources/end_game.wav");
        end.setVolume(6.0f);
        end.play();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                music.setVolume(0.0f);
            }
        };
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(task, 8000);
    }

    private void drawEntities(Graphics g) {
        try {
            turret.draw(g);
            for(Entity e : bullets)
                e.draw(g);
            for(Entity e : bricks)
                e.draw(g);
        } catch(ConcurrentModificationException cme) {
        }
        if(showMessage) {
            String message = "Press escape to exit...";

            g.setFont(new Font("moon", Font.BOLD, 24));
            float h, s, b;
            h = 336f / 360;
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

        if(leftPressed && !rightPressed) {
            turret.leftRotate();
        } else if(rightPressed && !leftPressed) {
            turret.rightRotate();
        }
        if(firePressed)
            fire();

        for(Entity bullet : bullets) {
            bricks.forEach((Entity brick) -> {
                if(bullet.isCollided(brick))
                    bullet.collided(brick);
            });
        }
        if(brickCount == 0)
            notifyWin();

        for(Entity e : bullets)
            e.move(DELAY);
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
