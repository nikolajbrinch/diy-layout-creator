package org.diylc.app.menus.file;

import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.diylc.core.SystemUtils;

public interface SaveDialog {

    public static SaveDialog newInstance(JFrame parent, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension) {
        if (SystemUtils.isMac()) {
            return new MacSaveDialog(parent, lastDirectory, initialFile, new FilenameFilterAdapter(filter), defaultExtension);
        } 
        
        return new DefaultSaveDialog(parent, lastDirectory, initialFile, filter, defaultExtension);
    }

    public Path show();
}
