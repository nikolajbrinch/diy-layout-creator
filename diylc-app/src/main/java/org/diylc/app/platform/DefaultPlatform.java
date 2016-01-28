package org.diylc.app.platform;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIManager;

import org.diylc.core.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seaglasslookandfeel.SeaGlassLookAndFeel;

public class DefaultPlatform implements Platform {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPlatform.class);

    private static DefaultPlatform defaultPlatform = new DefaultPlatform();

    public static Platform getInstance() {
        return defaultPlatform;
    }

    @Override
    public void setup() {
        LOG.debug("Java version: " + System.getProperty("java.runtime.version") + " by " + System.getProperty("java.vm.vendor"));
        LOG.debug("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));

        setLookAndFeel();
    }

    @Override
    public void setAbouthandler(AboutHandler aboutHandler) {
    }

    @Override
    public void setPreferencesHandler(PreferencesHandler preferencesHandler) {
    }

    @Override
    public void setQuitHandler(QuitHandler quitHandler) {
    }

    protected Font getSystemFont() {
        return new Font(SystemUtils.getDefaultDisplayFontName(), Font.PLAIN, (int) javafx.scene.text.Font.getDefault().getSize());
    }

    protected Font getDerivedInterfaceFont(Font originalFont) {
        return getSystemFont().deriveFont(originalFont.getStyle(), originalFont.getSize2D());
    }

    public String getDefaultFontName() {
        return getSystemFont().getName();
    }

    protected void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOG.error("Could not set Look&Feel", e);
        }

        Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);

            if (value instanceof Font) {
                Font font = (Font) value;
                if (font.getName().startsWith("Lucida")) {
                    Font derivedInterfaceFont = getDerivedInterfaceFont(font);

                    if (derivedInterfaceFont != null) {
                        UIManager.put(key, derivedInterfaceFont);
                    }
                }
            }
        }
    }
}
