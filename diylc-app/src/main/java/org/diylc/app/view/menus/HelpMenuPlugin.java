package org.diylc.app.view.menus;

import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.HelpController;
import org.diylc.app.model.Model;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.platform.AboutEvent;
import org.diylc.core.platform.Platform;
import org.diylc.core.utils.SystemUtils;

/**
 * Entry point class for help-related utilities.
 *
 * @author Branislav Stojkovic
 */
public class HelpMenuPlugin extends AbstractMenuPlugin<HelpController> {

    public HelpMenuPlugin(HelpController helpController, View view, Model model) {
        super(helpController, view, model);
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        addMenuAction(new GenericAction("User Manual", AppIconLoader.Manual.getIcon(), (event) -> getController().userManual()), MenuConstants.HELP_MENU);
        addMenuAction(new GenericAction("FAQ", AppIconLoader.Faq.getIcon(), (event) -> getController().faq()), MenuConstants.HELP_MENU);
        addMenuAction(new GenericAction("Component API", AppIconLoader.Component.getIcon(), (event) -> getController().componentApi()),
                MenuConstants.HELP_MENU);
        addMenuAction(new GenericAction("Plugin API", AppIconLoader.Plugin.getIcon(), (event) -> getController().pluginApi()), MenuConstants.HELP_MENU);
        addMenuAction(new GenericAction("Submit a Bug", AppIconLoader.Bug.getIcon(), (event) -> getController().submitBug()), MenuConstants.HELP_MENU);
        addMenuSeparator(MenuConstants.HELP_MENU);
        addMenuAction(new GenericAction("Donate", AppIconLoader.Donate.getIcon(), (event) -> getController().donate()), MenuConstants.HELP_MENU);

        if (!SystemUtils.isMac()) {
            addMenuAction(new GenericAction("About", AppIconLoader.About.getIcon(), (event) -> getController().about()), MenuConstants.HELP_MENU);
        }

        Platform.getPlatform().setAbouthandler((AboutEvent e) -> getController().about());
    }
    
}
