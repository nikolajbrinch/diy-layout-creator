package org.diylc.app.menus.file;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

public class FilenameFilterAdapter implements FilenameFilter {

    private FileFilter filter;

    public FilenameFilterAdapter(FileFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(File dir, String name) {
        return filter.accept(new File(dir, name));
    }

}
