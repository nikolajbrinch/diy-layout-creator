package org.diylc.app.platform;

public class DefaultQuitResponse extends AbstractAppResponse implements QuitResponse {

    public DefaultQuitResponse() {
        super(null);
    }

    @Override
    public void performQuit() {
        System.exit(0);
    }

    @Override
    public void cancelQuit() {
        /*
         * NOOP
         */
    }

}
