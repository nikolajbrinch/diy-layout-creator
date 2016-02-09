package org.diylc.app.view.menus;

import javax.swing.Action;

import org.diylc.app.Layer;
import org.diylc.app.actions.CheckBoxAction;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.LayersController;
import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

public class LayersMenuPlugin extends AbstractMenuPlugin<LayersController> {

    public LayersMenuPlugin(LayersController layersController, View view, Model model) {
        super(layersController, view, model);
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        for (Layer layer : Layer.values()) {
            final double zOrder = layer.getZOrder();

            Action lockAction = new CheckBoxAction("Lock", false, (event) -> getController().lock(event, zOrder));
            getController().addLockAction(layer, lockAction);

            Action selectAllAction = new GenericAction("Select All", (event) -> getController().selectAll(zOrder));
            getController().addSelectAllAction(zOrder, selectAllAction);

            addSubmenu(layer.getTitle(), null, MenuConstants.LAYERS_MENU);
            addMenuAction(lockAction, layer.getTitle());
            addMenuAction(selectAllAction, layer.getTitle());
        }
    }

}
