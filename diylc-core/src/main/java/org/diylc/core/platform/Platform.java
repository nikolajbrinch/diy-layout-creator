package org.diylc.core.platform;

import java.nio.file.Path;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.filechooser.FileFilter;

import org.diylc.core.components.properties.PropertyDescriptor;

public interface Platform {

    public static Platform getPlatform() {
        return PlatformStrategy.getPlatform();
    }

    public void setup();

    public void setAbouthandler(AboutHandler aboutHandler);

    public void setPreferencesHandler(PreferencesHandler preferencesHandler);
    
    public void setQuitHandler(QuitHandler quitHandler);

    public String getDefaultFontName();

    public String getDefaultDisplayFontName();
    
    public String getDefaultTextFontName();

    public String getDefaultMonospacedFontName();
    
    public SaveDialog createSaveDialog(JFrame parent, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension);

    public OpenDialog createOpenDialog(JFrame parent, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension,
            IFileChooserAccessory accessory);

    public JComponent createFontEditor(PropertyDescriptor property);

    public void setDefaultMenuBar(JMenuBar jMenuBar);

}
