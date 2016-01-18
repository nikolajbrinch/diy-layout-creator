package org.diylc.app.menus.file;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFrame;

public class MacSaveDialog implements SaveDialog {

    private final FileDialog fileDialog;

    private String defaultExtension;
    
    public MacSaveDialog(JFrame parent, File directory, File file, FilenameFilter filter, String defaultExtension) {
        this.defaultExtension = defaultExtension;
        fileDialog = new FileDialog(parent);

        if (directory != null) {
            fileDialog.setDirectory(directory.getAbsolutePath());
        }
        if (file != null) {
            fileDialog.setFile(file.getName());
        }
        if (filter != null) {
            fileDialog.setFilenameFilter(filter);
        }

        fileDialog.setMode(FileDialog.SAVE);
    }
    
    public File show() {
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();

        File file = null;
        
        if (directory != null && filename != null) {
            if (filename.indexOf('.') < 0) {
                filename += "." + defaultExtension;
            }
            
            file = new File(new File(directory), filename);
        }
        
        return file;
    }
}
