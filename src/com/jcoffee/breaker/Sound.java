package com.jcoffee.breaker;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The class <code>Sound</code> represents a playable sound file
 *
 * @author Adel Khial
 */

public class Sound {

    private Clip clip;
    private boolean loop = false;
    private boolean shot = false;

    public Sound(String ref) {
        URL url = this.getClass().getClassLoader().getResource(ref);

        if(url == null) {
            err("Can't find ref: " + ref);
        }

        try {
            clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip.open(ais);


        } catch(LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            err("Error opening file: " + ref);
        }
    }

    public void play() {
        if(loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } else if(shot) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    clip.stop();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 200); // shot sound length is 200 milliseconds to avoid clipping
        }
        clip.setFramePosition(0);
        clip.start();
    }

    public void stop() {
        clip.stop();
    }

    public void setVolume(float gain) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gain);
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setShot(boolean shot) {
        this.shot = shot;
    }

    private void err(String message) {
        System.err.println(message);
        System.exit(0);
    }
}
