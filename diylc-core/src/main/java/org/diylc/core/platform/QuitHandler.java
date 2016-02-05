package org.diylc.core.platform;


public interface QuitHandler extends AppEventHandler {

    public void handleQuit(QuitEvent event, QuitResponse response);

}
