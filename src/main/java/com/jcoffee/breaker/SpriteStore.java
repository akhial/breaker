package com.jcoffee.breaker;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * The singleton class <code>SpriteStore</code> handles sprite retrieval and caching.
 *
 * @author Kevin Glass
 */

public class SpriteStore {

    private static final SpriteStore instance = new SpriteStore();
    private final HashMap<String, Sprite> sprites = new HashMap<>();

    private SpriteStore() {} // override default constructor to prevent initialisation

    public static SpriteStore getInstance() {
        return instance;
    }

    public Sprite getSprite(String ref) {
        if(sprites.get(ref) != null) {
            return sprites.get(ref);
        }
        BufferedImage source = null;
        try {
            URL url = this.getClass().getClassLoader().getResource(ref);
            if(url == null)
                err("Can't find ref : " + ref);
            else
                source = ImageIO.read(url);

        } catch(IOException e) {
            err("Failed to load : " + ref);
        }

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        assert source != null;
        Image image = gc.createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.BITMASK);

        image.getGraphics().drawImage(source, 0, 0, null);

        Sprite sprite = new Sprite(image);
        sprites.put(ref, sprite);

        return sprite;
    }

    private void err(String message) {
        System.err.println(message);
        System.exit(0);
    }
}
