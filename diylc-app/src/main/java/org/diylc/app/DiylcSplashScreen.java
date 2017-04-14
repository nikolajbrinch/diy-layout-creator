package org.diylc.app;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.util.concurrent.locks.ReentrantLock;

import org.diylc.core.ProgressView;
import org.diylc.core.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SplashScreen for the program
 * 
 * @author nikolajbrinch@gmail.com
 */
public class DiylcSplashScreen implements ProgressView {

  private static final Logger LOG = LoggerFactory.getLogger(DiylcSplashScreen.class);

  public static int WIDTH = 729;

  public static int HEIGHT = 408;

  private Graphics2D graphics;

  private Font font;

  private SplashScreen splashScreen;

  private ReentrantLock updateLock = new ReentrantLock();

  public DiylcSplashScreen() {
    this.splashScreen = SplashScreen.getSplashScreen();

    if (splashScreen == null) {
      LOG.error("Splash screen is null");
    }

    if (splashScreen != null) {
      graphics = splashScreen.createGraphics();
      graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    }

    font = new Font(Platform.getPlatform().getDefaultTextFontName(), Font.PLAIN, 12);

    update("Loading application...");
  }

  @Override
  public void update(String message) {
    if (updateLock.tryLock()) {
      SplashScreen splashScreen = SplashScreen.getSplashScreen();
      if (splashScreen != null && splashScreen.isVisible()) {
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
  }

  public void dispose() {
    if (splashScreen != null) {
      if (splashScreen.isVisible()) {
        splashScreen.close();
      }
      splashScreen = null;
      graphics = null;
      font = null;
    }
  }

}
