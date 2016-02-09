package org.diylc.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemResource implements Resource {

    private final String filename;
    
    private final String fullpath;

    public FileSystemResource(String fullpath, String filename) {
        this.fullpath = fullpath;
        this.filename = filename;
    }

    public Path toPath() {
        return Paths.get(fullpath);
    }

    public File toFile() {
        return new File(fullpath);
    }

    public String getFilename() {
        return filename;
    }

    public InputStream openStream() throws IOException {
        return Files.newInputStream(toPath());
    }

    @Override
    public String toString() {
        return filename;
    }

}
