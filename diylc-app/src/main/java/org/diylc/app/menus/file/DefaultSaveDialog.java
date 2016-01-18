package org.diylc.app.menus.file;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.diylc.app.dialogs.OverwritePromptFileChooser;

public class DefaultSaveDialog implements SaveDialog {

    private final JFileChooser fileChooser;

    private JFrame parent;

    private String defaultExtension;

    public DefaultSaveDialog(JFrame parent, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension) {
        this.parent = parent;
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

        if (lastDirectory != null) {
            fileChooser.setCurrentDirectory(lastDirectory.toFile());
        }

        fileChooser.setSelectedFile(initialFile.toFile());
    }

    public Path show() {
        Path path = null;

        int result = fileChooser.showSaveDialog(parent);

        fileChooser.setAccessory(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().toPath();

            if (!fileChooser.getSelectedFile().getAbsolutePath().contains(".")) {
                return Paths.get(path.toString(), "." + defaultExtension);
            }
        }

        return path;
    }
}
