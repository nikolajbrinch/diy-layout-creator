package org.diylc.core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.util.EnumSet;

import org.diylc.core.events.EventListener;
import org.diylc.core.events.EventReciever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiylcSplashScreen {

    private static final Logger LOG = LoggerFactory.getLogger(DiylcSplashScreen.class);

    public static int WIDTH = 729;

    public static int HEIGHT = 408;

    private final EventReciever<EventType> eventReciever = new EventReciever<EventType>();

    private SplashScreen splashScreen;

    private Graphics2D graphics;

    private Font font;

    public DiylcSplashScreen() {
        splashScreen = SplashScreen.getSplashScreen();
        if (splashScreen == null) {
            LOG.error("Splash screen is null");
        }

        eventReciever.registerListener(EnumSet.of(EventType.SPLASH_UPDATE), new EventListener<EventType>() {

            @Override
            public void processEvent(EventType eventType, Object... params) {
                if (eventType == EventType.SPLASH_UPDATE) {
                    String message = (String) params[0];

                    updateText(message);
                }
            }
        });

        if (splashScreen != null) {
            graphics = splashScreen.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        }

        font = new Font(SystemUtils.getDefaultTextFontName(), Font.PLAIN, 12);
        updateText("Loading application...");
    }

    private void updateText(String message) {
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
