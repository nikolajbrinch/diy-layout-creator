package org.diylc.app;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This bootstrap, makes it possible for the application to restart. The
 * mechanism boots a second JVM with the real application, and looks for the
 * returncode from that application to determine, if the real application should
 * be started again, or the program should exit. In this way, we can manipulate
 * options in the real application and conveniently restart it for the user, or
 * do automatic updates at some point.
 * 
 * @author nikolajbrinch@gmail.com
 */
public class DiylcBootStrap {

    static final Logger LOG = LoggerFactory.getLogger(DiylcBootStrap.class);

    private BootConfiguration bootConfiguration;

    public DiylcBootStrap(BootConfiguration bootConfiguration) {
        this.bootConfiguration = bootConfiguration;
    }

    public void run() throws InterruptedException, IOException {
        ProcessBuilder processBuilder = bootConfiguration.newProcessBuilder();

        processBuilder.inheritIO().start();
    }

    /**
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        new DiylcBootStrap(new BootConfigurationBuilder(args).setMainClassName(DIYLCStarter.class.getName()).build()).run();
    }

}
