package org.diylc.app.view.menus;

import org.diylc.app.actions.CheckBoxAction;
import org.diylc.app.controllers.ConfigController;
import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.config.Configuration;

/**
 * Controls configuration menu.
 * 
 * @author Branislav Stojkovic
 */
public class ConfigMenuPlugin extends AbstractMenuPlugin<ConfigController> {

    public ConfigMenuPlugin(ConfigController configController, View view, Model model) {
        super(configController, view, model);
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        addMenuAction(new CheckBoxAction("Auto-Create Pads", Configuration.INSTANCE.getAutoCreatePads(), (event) -> getController()
                .autoCreatePads(event)), MenuConstants.CONFIG_MENU);
        addMenuAction(
                new CheckBoxAction("Auto-Edit Mode", Configuration.INSTANCE.getAutoEdit(), (event) -> getController().autoEditMode(event)),
                MenuConstants.CONFIG_MENU);
        addMenuAction(new CheckBoxAction("Continuous Creation", Configuration.INSTANCE.getContinuousCreation(), (event) -> getController()
                .continuousCreation(event)), MenuConstants.CONFIG_MENU);
        addMenuAction(new CheckBoxAction("Export Grid", Configuration.INSTANCE.getExportGrid(), (event) -> getController()
                .exportGrid(event)), MenuConstants.CONFIG_MENU);
        addMenuAction(
                new CheckBoxAction("Snap to Grid", Configuration.INSTANCE.getSnapToGrip(), (event) -> getController().snapToGrip(event)),
                MenuConstants.CONFIG_MENU);
        addMenuAction(new CheckBoxAction("Sticky Points", Configuration.INSTANCE.getStickyPoints(), (event) -> getController()
                .stickyPoints(event)), MenuConstants.CONFIG_MENU);
    }
}
