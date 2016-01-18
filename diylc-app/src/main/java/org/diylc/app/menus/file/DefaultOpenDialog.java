package org.diylc.app.menus.file;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.diylc.app.dialogs.IFileChooserAccessory;
import org.diylc.app.dialogs.OverwritePromptFileChooser;

public class DefaultOpenDialog implements OpenDialog {

    public final JFileChooser fileChooser;
    
    private final Component parent;
    
    private final String defaultExtension;

    private File directory;

    public DefaultOpenDialog(Component parent, File directory, File file, FileFilter filter, String defaultExtension, IFileChooserAccessory accessory) {
        this.parent = parent;
        this.directory = directory;
        this.defaultExtension = defaultExtension;
        fileChooser = new JFileChooser();

        if (accessory != null) {
            accessory.install(fileChooser);
        }
        for (FileFilter fileFilter : fileChooser.getChoosableFileFilters()) {
            fileChooser.removeChoosableFileFilter(fileFilter);
        }
        if (fileChooser instanceof OverwritePromptFileChooser) {
            ((OverwritePromptFileChooser) fileChooser).setFileFilter(filter, defaultExtension);
        } else {
            fileChooser.setFileFilter(filter);
        }
        if (directory != null) {
            fileChooser.setCurrentDirectory(directory);
        }
        
        fileChooser.setSelectedFile(file);
    }

    @Override
    public File show() {
        int result = fileChooser.showOpenDialog(parent);

        fileChooser.setAccessory(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            directory = fileChooser.getCurrentDirectory();
            if (fileChooser.getSelectedFile().getAbsolutePath().contains(".")) {
                return fileChooser.getSelectedFile();
            } else {
                return new File(fileChooser.getSelectedFile().getAbsoluteFile() + "." + defaultExtension);
            }
        } else {
            return null;
        }
    }
}
