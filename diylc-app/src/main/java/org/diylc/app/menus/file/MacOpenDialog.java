package org.diylc.app.menus.file;

import java.awt.FileDialog;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;

public class MacOpenDialog implements OpenDialog {

    private FileDialog fileDialog;

    public MacOpenDialog(JFrame parent, Path lastDirectory, Path initialFile, FilenameFilter filter, String defaultExtension) {
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

        fileDialog.setMode(FileDialog.LOAD);
    }

    @Override
    public Path show() {
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();

        return directory != null && filename != null ? Paths.get(directory,  filename) : null;
    }

}
