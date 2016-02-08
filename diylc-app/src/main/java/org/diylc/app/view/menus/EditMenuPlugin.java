package org.diylc.app.view.menus;

import org.diylc.app.Accelerators;
import org.diylc.app.ExpansionMode;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.EditMenuController;
import org.diylc.app.model.Model;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

public class EditMenuPlugin extends AbstractMenuPlugin<EditMenuController> {

    public EditMenuPlugin(EditMenuController editMenuController, View view, Model model) {
        super(editMenuController, view, model);
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        addMenuAction(getController().getUndoHandler().getUndoAction(), MenuConstants.EDIT_MENU);
        addMenuAction(getController().getUndoHandler().getRedoAction(), MenuConstants.EDIT_MENU);
        addMenuSeparator(MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("Cut", AppIconLoader.Cut.getIcon(), Accelerators.CUT, (event) -> getController().cut()), MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("Copy", AppIconLoader.Copy.getIcon(), Accelerators.COPY, (event) -> getController().copy()), MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("Paste", AppIconLoader.Paste.getIcon(), Accelerators.PASTE, (event) -> getController().paste()), MenuConstants.EDIT_MENU);
        addMenuSeparator(MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("Select All", AppIconLoader.Selection.getIcon(), Accelerators.SELECT_ALL, (event) -> getController().selectAll()), MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("Delete Selection", AppIconLoader.Delete.getIcon(), Accelerators.DELETE,
                (event) -> getController().delete()), MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("Edit Selection", AppIconLoader.EditComponent.getIcon(), Accelerators.EDIT_SELECTION,
                (event) -> getController().edit()), MenuConstants.EDIT_MENU);
        addMenuSeparator(MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("Save as Template", AppIconLoader.BriefcaseAdd.getIcon(), (event) -> getController()
                .saveAsTemplate()), MenuConstants.EDIT_MENU);
        addSubmenu(MenuConstants.EDIT_RENUMBER_MENU, AppIconLoader.Sort.getIcon(), MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("X Axis First", (event) -> getController().renumber(true)), MenuConstants.EDIT_RENUMBER_MENU);
        addMenuAction(new GenericAction("Y Axis First", (event) -> getController().renumber(false)), MenuConstants.EDIT_RENUMBER_MENU);
        addSubmenu(MenuConstants.EDIT_EXPAND_MENU, AppIconLoader.BranchAdd.getIcon(), MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("All Connected", (event) -> getController().expandSelection(ExpansionMode.ALL)), MenuConstants.EDIT_EXPAND_MENU);
        addMenuAction(new GenericAction("Immediate Only", (event) -> getController().expandSelection(ExpansionMode.IMMEDIATE)), MenuConstants.EDIT_EXPAND_MENU);
        addMenuAction(new GenericAction("Same Type Only", (event) -> getController().expandSelection(ExpansionMode.SAME_TYPE)), MenuConstants.EDIT_EXPAND_MENU);
        addMenuSeparator(MenuConstants.EDIT_MENU);
        addMenuAction(new GenericAction("Edit Project", AppIconLoader.DocumentEdit.getIcon(), (event) -> getController().editProject()), MenuConstants.EDIT_MENU);

        getView().refreshActions();
    }

}
