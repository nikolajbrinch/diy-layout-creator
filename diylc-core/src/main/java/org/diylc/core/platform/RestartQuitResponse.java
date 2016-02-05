package org.diylc.core.platform;

public class RestartQuitResponse extends AbstractAppResponse implements QuitResponse {

    public RestartQuitResponse() {
        super(null);
    }

    @Override
    public void performQuit() {
        System.exit(RESTART);
    }

    @Override
    public void cancelQuit() {
        /*
         * NOOP
         */
    }

}
