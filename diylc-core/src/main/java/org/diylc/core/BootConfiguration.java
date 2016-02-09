package org.diylc.core;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BootConfiguration, that can create ProcessBuilders, ready to execute.
 * 
 * @author nikolajbrinch@gmail.com
 */
public class BootConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(BootConfiguration.class);

    private final File workingDirectory;
    
    private final List<String> command;

    public BootConfiguration(File workingDirectory, List<String> command) {
        this.workingDirectory = workingDirectory;
        this.command = command;
    }

    public ProcessBuilder newProcessBuilder() {
        LOG.debug("Launching command: " + String.join("\n\t", command));
        
        return new ProcessBuilder(command).directory(workingDirectory);
    }
}