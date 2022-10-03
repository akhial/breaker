package com.jcoffee.breaker;

import java.awt.EventQueue;

import javax.swing.*;

/**
 * The <code>Application</code> class is the entry point to the game application.
 *
 * @author Adel Khial
 */

public class Application extends JFrame {

    private Application() {
        Board b = new Board();
        add(b);

        KeyInputHandler kHandler = new KeyInputHandler(b);
        addKeyListener(kHandler);

        MouseInputHandler mHandler = new MouseInputHandler(b);
        addMouseListener(mHandler);

        pack();
        setResizable(false);
        setTitle("Breaker");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Application ex = new Application();
            ex.setVisible(true);
        });
    }
}
