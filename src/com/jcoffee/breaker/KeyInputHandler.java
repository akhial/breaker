package com.jcoffee.breaker;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInputHandler extends KeyAdapter {

    private Board board;

    public KeyInputHandler(Board board) {
        this.board = board;
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            board.setGameRunning(false);
    }

    public void keyTyped(KeyEvent e) {
    }
}
