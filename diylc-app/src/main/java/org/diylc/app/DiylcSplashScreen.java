package org.diylc.app;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.util.EnumSet;

import org.diylc.core.EventType;
import org.diylc.core.events.EventListener;
import org.diylc.core.events.EventReciever;
import org.diylc.core.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SplashScreen for the program
 * 
 * @author nikolajbrinch@gmail.com
 */
public class DiylcSplashScreen implements EventListener<EventType> {

    private static final Logger LOG = LoggerFactory.getLogger(DiylcSplashScreen.class);

    public static int WIDTH = 729;

    public static int HEIGHT = 408;

    private EventReciever<EventType> eventReciever = new EventReciever<EventType>();

    private Graphics2D graphics;

    private Font font;

    private SplashScreen splashScreen;

    public DiylcSplashScreen() {
        this.splashScreen = SplashScreen.getSplashScreen();

        if (splashScreen == null) {
            LOG.error("Splash screen is null");
        }

        if (splashScreen != null) {
            graphics = splashScreen.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        }

        font = new Font(Platform.getPlatform().getDefaultTextFontName(), Font.PLAIN, 12);

        eventReciever.registerListener(EnumSet.of(EventType.SPLASH_UPDATE), this);

        updateText("Loading application...");
    }

    public void processEvent(EventType eventType, Object... params) {
        if (eventType == EventType.SPLASH_UPDATE) {
            updateText((String) params[0]);
        }
    }

    private void updateText(String message) {
        SplashScreen splashScreen = SplashScreen.getSplashScreen();
        if (splashScreen.isVisible()) {
            if (graphics != null) {
                graphics.setComposite(AlphaComposite.Clear);
                graphics.fillRect(0, 0, WIDTH, HEIGHT);
                graphics.setPaintMode();
                graphics.setFont(font);
                graphics.setColor(Color.black);
                graphics.drawString(message, 35, HEIGHT - 35);
                splashScreen.update();
            }
        }
    }

    public void dispose() {
        eventReciever.unregisterListener(this);
        if (splashScreen != null) {
            if (splashScreen.isVisible()) {
                splashScreen.close();
            }
            splashScreen = null;
            graphics = null;
            font = null;
            eventReciever = null;
        }
    }
}
