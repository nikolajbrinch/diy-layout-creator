package org.diylc.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JarResource implements Resource {

    private final String filename;

    public JarResource(String filename) {
        this.filename = filename;
    }
    
    public URL toUrl() throws IOException {
        return new URL(URLEncoder.encode(getFilename(),  Charset.defaultCharset().name()));
    }
    
    public InputStream openStream() throws IOException {
        return toUrl().openStream();
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return filename;
    }

    @Override
    public Path toPath() throws IOException {
        try {
            return Paths.get(toUrl().toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
