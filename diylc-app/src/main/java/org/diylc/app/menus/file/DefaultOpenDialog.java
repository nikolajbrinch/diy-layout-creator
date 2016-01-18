package org.diylc.app.menus.file;

import java.awt.Component;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.diylc.app.dialogs.IFileChooserAccessory;
import org.diylc.app.dialogs.OverwritePromptFileChooser;

public class DefaultOpenDialog implements OpenDialog {

    public final JFileChooser fileChooser;

    private final Component parent;

    private final String defaultExtension;

    public DefaultOpenDialog(Component parent, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension,
            IFileChooserAccessory accessory) {
        this.parent = parent;
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
        if (lastDirectory != null) {
            fileChooser.setCurrentDirectory(lastDirectory.toFile());
        }

        fileChooser.setSelectedFile(initialFile.toFile());
    }

    @Override
    public Path show() {
        Path path = null;

        int result = fileChooser.showOpenDialog(parent);

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
