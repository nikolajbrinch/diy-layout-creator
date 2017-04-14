package org.diylc.app;

import java.io.IOException;

import org.diylc.core.BootConfiguration;
import org.diylc.core.BootConfigurationBuilder;
import org.diylc.core.utils.SystemUtils;

public class Restarter implements Runnable {

  private final BootConfiguration bootConfiguration;

  public Restarter(String[] args) {
    BootConfigurationBuilder bootConfigurationBuilder = new BootConfigurationBuilder(args)
        .setMainClassName("org.diylc.app.DIYLCStarter")
        .addInputArgument("-splash:" + System.getProperty("org.diylc.splashImageLocation"));

    if (SystemUtils.isMac()) {
      bootConfigurationBuilder
          .addInputArgument("-Xdock:name", System.getProperty("org.diylc.appName"))
          .addInputArgument("-Xdock:icon", System.getProperty("org.diylc.appIcon"));
    }

    this.bootConfiguration = bootConfigurationBuilder.build();
  }

  @Override
  public void run() {
    try {
      bootConfiguration.newProcessBuilder().inheritIO().start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
