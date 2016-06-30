package org.diylc.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface Resource {

    public String getFilename();

    public InputStream openStream() throws IOException;

    public Path toPath() throws IOException;

    public URL toUrl() throws IOException;

}
