package org.diylc.app.utils;

import java.util.Arrays;

import org.diylc.core.utils.SystemUtils;

/**
 * @author nikolajbrinch@gmail.com
 */
public class BrowserUtils {

    static final String[] BROWSERS = { "google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori", "kazehakase",
            "mozilla" };
    
    static final String ERROR_MESSAGE = "Error attempting to launch web browser";

    public static void openURL(String url) throws Exception {
        try {
            /*
             * attempt to use Desktop library from JDK 1.6+
             */
            Class<?> dekstop = Class.forName("java.awt.Desktop");
            dekstop.getDeclaredMethod("browse", new Class[] { java.net.URI.class }).invoke(
                    dekstop.getDeclaredMethod("getDesktop").invoke(null), new Object[] { java.net.URI.create(url) });
            /*
             * above code mimicks: java.awt.Desktop.getDesktop().browse()
             */
        } catch (Exception ignore) {
            /*
             * library not available or failed
             */
            if (SystemUtils.isMac()) {
                Class.forName("com.apple.eio.FileManager").getDeclaredMethod("openURL", new Class[] { String.class })
                        .invoke(null, new Object[] { url });
            } else if (SystemUtils.isWindows()) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                /*
                 * assume Unix or Linux
                 */
                String browser = null;
                for (String b : BROWSERS) {
                    if (browser == null && Runtime.getRuntime().exec(new String[] { "which", b }).getInputStream().read() != -1) {
                        Runtime.getRuntime().exec(new String[] { browser = b, url });
                    }
                }
                if (browser == null) {
                    throw new Exception(Arrays.toString(BROWSERS));
                }
            }
        }
    }
}
