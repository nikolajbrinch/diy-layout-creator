package org.diylc.app.controllers;

import org.diylc.app.model.Model;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.utils.BrowserUtils;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.app.view.dialogs.AboutDialog;
import org.diylc.app.view.dialogs.DialogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpController extends AbstractController {

    static final Logger LOG = LoggerFactory.getLogger(HelpController.class);

    public static String MANUAL_URL = "http://code.google.com/p/diy-layout-creator/wiki/Manual";

    public static String FAQ_URL = "http://code.google.com/p/diy-layout-creator/wiki/FAQ";

    public static String COMPONENT_URL = "http://code.google.com/p/diy-layout-creator/wiki/ComponentAPI";

    public static String PLUGIN_URL = "http://code.google.com/p/diy-layout-creator/wiki/PluginAPI";

    public static String BUG_URL = "http://code.google.com/p/diy-layout-creator/issues/entry";

    public static String DONATE_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=25161";

    private AboutDialog aboutDialog;

    public HelpController(ApplicationController applicationController, View view, Model model, DrawingController controller, IPlugInPort plugInPort) {
        super(applicationController, view, model, controller, plugInPort);
    }

    public void userManual() {
        openBrowser(MANUAL_URL);
    }

    public void faq() {
        openBrowser(FAQ_URL);
    }

    public void componentApi() {
        openBrowser(COMPONENT_URL);
    }

    public void pluginApi() {
        openBrowser(PLUGIN_URL);
    }

    public void submitBug() {
        openBrowser(BUG_URL);
    }

    public void donate() {
        openBrowser(DONATE_URL);
    }

    public void about() {
        getAboutDialog().setVisible(true);
    }

    private AboutDialog getAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = DialogFactory.getInstance().createAboutDialog("DIY Layout Creator", AppIconLoader.IconLarge.getIcon(),
                    getModel().getCurrentVersionNumber().toString(), "Branislav Stojkovic", "diylc.org", "bancika@gmail.com", "");
        }
        return aboutDialog;
    }

    private void openBrowser(String url) {
        try {
            BrowserUtils.openURL(url);
        } catch (Exception e1) {
            LOG.error("Could not launch default browser", e1);
        }
    }

}
