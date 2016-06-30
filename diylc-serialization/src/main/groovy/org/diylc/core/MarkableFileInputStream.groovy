package org.diylc.core

import java.nio.channels.FileChannel

public class MarkableFileInputStream extends FilterInputStream {
    
    private FileChannel fileChannel
    
    private long mark = -1

    public MarkableFileInputStream(FileInputStream fis) {
        super(fis)
        fileChannel = fis.getChannel()
    }

    @Override
    public boolean markSupported() {
        return true
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            mark = fileChannel.position()
        } catch (IOException ex) {
            mark = -1
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (mark == -1) {
            throw new IOException("not marked")
        }
        fileChannel.position(mark)
    }
}