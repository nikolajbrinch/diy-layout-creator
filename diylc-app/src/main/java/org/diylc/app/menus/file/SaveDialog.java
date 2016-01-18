package org.diylc.app.menus.file;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.diylc.core.SystemUtils;

public interface SaveDialog {

    public static SaveDialog newInstance(JFrame parent, File directory, File file, FileFilter filter, String defaultExtension) {
        if (SystemUtils.isMac()) {
            return new MacSaveDialog(parent, directory, file, new FilenameFilterAdapter(filter), defaultExtension);
        } 
        
        return new DefaultSaveDialog(parent, directory, file, filter, defaultExtension);
    }

    public File show();
}
