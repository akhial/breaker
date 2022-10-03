package com.jcoffee.breaker;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The <code>MouseInputHandler</code> class manages mouse clicks.
 *
 * @author Adel Khial
 */

public class MouseInputHandler extends MouseAdapter {

    private final Board board;

    public MouseInputHandler(Board board) {
        this.board = board;
    }

    public void mouseClicked(MouseEvent e) {
        board.checkBricks(e.getX(), e.getY() - 25); // the y-axis starts at 25
    }

}
