package org.diylc.core.platform;

public class DefaultQuitResponse extends AbstractAppResponse implements QuitResponse {

    public DefaultQuitResponse() {
        super(null);
    }

    @Override
    public void performQuit() {
        System.exit(EXIT);
    }

    @Override
    public void cancelQuit() {
        /*
         * NOOP
         */
    }

}
