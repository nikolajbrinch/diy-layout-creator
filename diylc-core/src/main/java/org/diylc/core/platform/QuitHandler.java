package org.diylc.core.platform;


@FunctionalInterface
public interface QuitHandler extends AppEventHandler {

    public void handleQuit(QuitEvent event, QuitResponse response);

}
