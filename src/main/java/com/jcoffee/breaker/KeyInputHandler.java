package com.jcoffee.breaker;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The <code>KeyInputHandler</code> class manages keyboard input.
 *
 * @author Adel Khial
 */

public class KeyInputHandler extends KeyAdapter {

    private final Board board;

    public KeyInputHandler(Board board) {
        this.board = board;
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            board.stopGame();
    }
}
