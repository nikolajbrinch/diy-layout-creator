package org.diylc.app.menus.file;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.diylc.app.dialogs.IFileChooserAccessory;
import org.diylc.core.SystemUtils;

public interface OpenDialog {

    public static OpenDialog newInstance(JFrame mainFrame, File directory, File file, FileFilter filter, String defaultExtension, IFileChooserAccessory accessory) {
        if (SystemUtils.isMac()) {
            return new MacOpenDialog(mainFrame, directory, file, new FilenameFilterAdapter(filter), defaultExtension);
        }
        
        return new DefaultOpenDialog(mainFrame, directory, file, filter, defaultExtension, accessory);
    }
    
    public File show();

}
