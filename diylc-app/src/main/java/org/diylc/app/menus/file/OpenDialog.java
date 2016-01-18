package org.diylc.app.menus.file;

import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.diylc.app.dialogs.IFileChooserAccessory;
import org.diylc.core.SystemUtils;

public interface OpenDialog {

    public static OpenDialog newInstance(JFrame mainFrame, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension, IFileChooserAccessory accessory) {
        if (SystemUtils.isMac()) {
            return new MacOpenDialog(mainFrame, lastDirectory, initialFile, new FilenameFilterAdapter(filter), defaultExtension);
        }
        
        return new DefaultOpenDialog(mainFrame, lastDirectory, initialFile, filter, defaultExtension, accessory);
    }
    
    public Path show();

}
