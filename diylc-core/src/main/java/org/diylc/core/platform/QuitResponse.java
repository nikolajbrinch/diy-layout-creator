package org.diylc.core.platform;


public interface QuitResponse {

    public static final int EXIT = 0;

    public static final int RESTART = 255;

    public void performQuit();
    
    public void cancelQuit();

}
