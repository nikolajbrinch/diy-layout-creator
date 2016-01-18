package org.diylc.app.menus.file;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFrame;

public class MacOpenDialog implements OpenDialog {

    private FileDialog fileDialog;

    public MacOpenDialog(JFrame parent, File directory, File file, FilenameFilter filter, String defaultExtension) {
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

        fileDialog.setMode(FileDialog.LOAD);
    }

    @Override
    public File show() {
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();

        return directory != null && filename != null ? new File(new File(directory), filename) : null;
    }

}
