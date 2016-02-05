package org.diylc.core.platform;

import java.awt.Font;
import java.nio.file.Path;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.diylc.core.PropertyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPlatform implements Platform {

    private static final Logger LOG = LoggerFactory.getLogger(Platform.class);

    private static DefaultPlatform defaultPlatform = new DefaultPlatform();

    public static Platform getInstance() {
        return defaultPlatform;
    }

    @Override
    public void setup() {
        LOG.info("Java version: " + System.getProperty("java.runtime.version") + " by " + System.getProperty("java.vm.vendor"));
        LOG.info("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));

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
        return new Font(getDefaultDisplayFontName(), Font.PLAIN, UIManager.getFont("Label.font").getSize());
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

    @Override
    public String getDefaultDisplayFontName() {
        return "Tahoma";
    }

    @Override
    public String getDefaultMonospacedFontName() {
        return "Courier New";
    }

    @Override
    public String getDefaultTextFontName() {
        return "Arial";
    }

    @Override
    public SaveDialog createSaveDialog(JFrame parent, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension) {
        return new DefaultSaveDialog(parent, lastDirectory, initialFile, filter, defaultExtension);
    }

    @Override
    public OpenDialog createOpenDialog(JFrame mainFrame, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension,
            IFileChooserAccessory accessory) {
        return new DefaultOpenDialog(mainFrame, lastDirectory, initialFile, filter, defaultExtension, accessory);
    }

    @Override
    public JComponent createFontEditor(PropertyWrapper property) {
        return new DefaultFontEditor(property);
    }

    @Override
    public void setDefaultMenuBar(JMenuBar jMenuBar) {
        // TODO Auto-generated method stub
        
    }
}
