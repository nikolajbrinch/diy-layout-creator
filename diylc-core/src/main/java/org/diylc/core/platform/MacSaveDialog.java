package org.diylc.core.platform;

import java.awt.FileDialog;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;

public class MacSaveDialog implements SaveDialog {

    private final FileDialog fileDialog;

    private String defaultExtension;
    
    public MacSaveDialog(JFrame parent, Path lastDirectory, Path initialFile, FilenameFilter filter, String defaultExtension) {
        this.defaultExtension = defaultExtension;
        fileDialog = new FileDialog(parent);

        if (lastDirectory != null) {
            fileDialog.setDirectory(lastDirectory.toAbsolutePath().toString());
        }
        if (initialFile != null) {
            fileDialog.setFile(initialFile.getFileName().toString());
        }
        if (filter != null) {
            fileDialog.setFilenameFilter(filter);
        }

        fileDialog.setMode(FileDialog.SAVE);
    }
    
    public Path show() {
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();

        Path path = null;
        
        if (directory != null && filename != null) {
            if (filename.indexOf('.') < 0) {
                filename += "." + defaultExtension;
            }
            
            path = Paths.get(directory, filename);
        }
        
        return path;
    }
}
