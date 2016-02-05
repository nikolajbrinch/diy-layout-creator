package org.diylc.app.view.menus;

import org.diylc.app.Accelerators;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.ArrangeMenuController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

/**
 * @author nikolajbrinch@gmail.com
 */
public class ArrangeMenuPlugin extends AbstractMenuPlugin<ArrangeMenuController> {

    public ArrangeMenuPlugin(ArrangeMenuController arrangeController, View view, DrawingModel model) {
        super(arrangeController, view, model);
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        addMenuAction(new GenericAction("Send Backward", AppIconLoader.Back.getIcon(), Accelerators.SEND_TO_BACK,
                (event) -> getController().sendBackward()), MenuConstants.ARRANGE_MENU);
        addMenuAction(new GenericAction("Bring Forward", AppIconLoader.Front.getIcon(), Accelerators.BRING_TO_FRONT,
                (event) -> getController().bringForward()), MenuConstants.ARRANGE_MENU);
        addMenuSeparator(MenuConstants.ARRANGE_MENU);
        addMenuAction(new GenericAction("Rotate Clockwise", AppIconLoader.RotateCW.getIcon(), Accelerators.ROTATE_RIGHT,
                (event) -> getController().rotateSelection(1)), MenuConstants.ARRANGE_MENU);
        addMenuAction(new GenericAction("Rotate Counterclockwise", AppIconLoader.RotateCCW.getIcon(), Accelerators.ROTATE_LEFT,
                (event) -> getController().rotateSelection(-1)), MenuConstants.ARRANGE_MENU);
        addMenuSeparator(MenuConstants.ARRANGE_MENU);
        addMenuAction(new GenericAction("Group Selection", AppIconLoader.Group.getIcon(), Accelerators.GROUP, (event) -> getController()
                .group()), MenuConstants.ARRANGE_MENU);
        addMenuAction(new GenericAction("Ungroup Selection", AppIconLoader.Ungroup.getIcon(), Accelerators.UNGROUP,
                (event) -> getController().ungroup()), MenuConstants.ARRANGE_MENU);

        getView().refreshActions();
    }

}
