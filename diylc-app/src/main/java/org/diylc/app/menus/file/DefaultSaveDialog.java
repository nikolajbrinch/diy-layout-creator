package org.diylc.app.menus.file;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.diylc.app.dialogs.OverwritePromptFileChooser;
import org.diylc.core.config.Configuration;

public class DefaultSaveDialog implements SaveDialog {

    private final JFileChooser fileChooser;
    private JFrame parent;
    private String defaultExtension;
    private File directory;
    
    public DefaultSaveDialog(JFrame parent, File directory, File file, FileFilter filter, String defaultExtension) {
        this.parent = parent;
        this.directory = directory;
        this.defaultExtension = defaultExtension;
        fileChooser = new OverwritePromptFileChooser();

        for (javax.swing.filechooser.FileFilter fileFilter : fileChooser.getChoosableFileFilters()) {
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
    
    public File show() {
        int result = fileChooser.showSaveDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            directory = fileChooser.getCurrentDirectory();
            Configuration.INSTANCE.setLastPath(directory.getAbsolutePath());
            
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
