package org.diylc.app.menus.edit;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.util.EnumSet;

import org.diylc.app.EventType;
import org.diylc.app.IPlugIn;
import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.core.Project;

public class EditMenuPlugin implements IPlugIn, ClipboardOwner {

    private static final String EDIT_TITLE = "Edit";
    private static final String RENUMBER_TITLE = "Renumber Selection";
    private static final String EXPAND_TITLE = "Expand Selection";

    private IPlugInPort plugInPort;
    private ISwingUI swingUI;

    private Clipboard clipboard;

    private EditActionFactory.CutAction cutAction;
    private EditActionFactory.CopyAction copyAction;
    private EditActionFactory.PasteAction pasteAction;
    private EditActionFactory.EditSelectionAction editSelectionAction;
    private EditActionFactory.DeleteSelectionAction deleteSelectionAction;
    private EditActionFactory.RenumberAction renumberXAxisAction;
    private EditActionFactory.RenumberAction renumberYAxisAction;
    private EditActionFactory.ExpandSelectionAction expandSelectionAllAction;
    private EditActionFactory.ExpandSelectionAction expandSelectionImmediateAction;
    private EditActionFactory.ExpandSelectionAction expandSelectionSameTypeAction;
    private EditActionFactory.SaveAsTemplateAction saveAsTemplateAction;

    private UndoHandler<Project> undoHandler;

    public EditMenuPlugin(ISwingUI swingUI) {
        this.swingUI = swingUI;
        // SecurityManager securityManager = System.getSecurityManager();
        // if (securityManager != null) {
        // try {
        // securityManager.checkSystemClipboardAccess();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        undoHandler = new UndoHandler<Project>(new IUndoListener<Project>() {

            @Override
            public void actionPerformed(Project currentState) {
                plugInPort.loadProject(currentState, false);
            }
        });
        clipboard.addFlavorListener(new FlavorListener() {

            @Override
            public void flavorsChanged(FlavorEvent e) {
                refreshActions();
            }
        });
    }

    public EditActionFactory.CutAction getCutAction() {
        if (cutAction == null) {
            cutAction = EditActionFactory.INSTANCE.createCutAction(plugInPort, clipboard, this);
        }
        return cutAction;
    }

    public EditActionFactory.CopyAction getCopyAction() {
        if (copyAction == null) {
            copyAction = EditActionFactory.INSTANCE.createCopyAction(plugInPort, clipboard, this);
        }
        return copyAction;
    }

    public EditActionFactory.PasteAction getPasteAction() {
        if (pasteAction == null) {
            pasteAction = EditActionFactory.INSTANCE.createPasteAction(plugInPort, clipboard);
        }
        return pasteAction;
    }

    public EditActionFactory.EditSelectionAction getEditSelectionAction() {
        if (editSelectionAction == null) {
            editSelectionAction = EditActionFactory.INSTANCE.createEditSelectionAction(plugInPort);
        }
        return editSelectionAction;
    }

    public EditActionFactory.DeleteSelectionAction getDeleteSelectionAction() {
        if (deleteSelectionAction == null) {
            deleteSelectionAction = EditActionFactory.INSTANCE.createDeleteSelectionAction(plugInPort);
        }
        return deleteSelectionAction;
    }

    public EditActionFactory.RenumberAction getRenumberXAxisAction() {
        if (renumberXAxisAction == null) {
            renumberXAxisAction = EditActionFactory.INSTANCE.createRenumberAction(plugInPort, true);
        }
        return renumberXAxisAction;
    }

    public EditActionFactory.RenumberAction getRenumberYAxisAction() {
        if (renumberYAxisAction == null) {
            renumberYAxisAction = EditActionFactory.INSTANCE.createRenumberAction(plugInPort, false);
        }
        return renumberYAxisAction;
    }

    public EditActionFactory.ExpandSelectionAction getExpandSelectionAllAction() {
        if (expandSelectionAllAction == null) {
            expandSelectionAllAction = EditActionFactory.INSTANCE.createExpandSelectionAction(plugInPort, ExpansionMode.ALL);
        }
        return expandSelectionAllAction;
    }

    public EditActionFactory.ExpandSelectionAction getExpandSelectionImmediateAction() {
        if (expandSelectionImmediateAction == null) {
            expandSelectionImmediateAction = EditActionFactory.INSTANCE.createExpandSelectionAction(plugInPort, ExpansionMode.IMMEDIATE);
        }
        return expandSelectionImmediateAction;
    }

    public EditActionFactory.ExpandSelectionAction getExpandSelectionSameTypeAction() {
        if (expandSelectionSameTypeAction == null) {
            expandSelectionSameTypeAction = EditActionFactory.INSTANCE.createExpandSelectionAction(plugInPort, ExpansionMode.SAME_TYPE);
        }
        return expandSelectionSameTypeAction;
    }

    public EditActionFactory.SaveAsTemplateAction getSaveAsTemplateAction() {
        if (saveAsTemplateAction == null) {
            saveAsTemplateAction = EditActionFactory.INSTANCE.createSaveAsTemplateAction(plugInPort);
        }
        return saveAsTemplateAction;
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        this.plugInPort = plugInPort;

        EditActionFactory actionFactory = EditActionFactory.INSTANCE;

        swingUI.injectMenuAction(undoHandler.getUndoAction(), EDIT_TITLE);
        swingUI.injectMenuAction(undoHandler.getRedoAction(), EDIT_TITLE);
        swingUI.injectMenuAction(null, EDIT_TITLE);
        swingUI.injectMenuAction(getCutAction(), EDIT_TITLE);
        swingUI.injectMenuAction(getCopyAction(), EDIT_TITLE);
        swingUI.injectMenuAction(getPasteAction(), EDIT_TITLE);
        swingUI.injectMenuAction(null, EDIT_TITLE);
        swingUI.injectMenuAction(actionFactory.createSelectAllAction(plugInPort), EDIT_TITLE);
        swingUI.injectMenuAction(getEditSelectionAction(), EDIT_TITLE);
        swingUI.injectMenuAction(getDeleteSelectionAction(), EDIT_TITLE);
        swingUI.injectMenuAction(null, EDIT_TITLE);
        swingUI.injectMenuAction(getSaveAsTemplateAction(), EDIT_TITLE);
        swingUI.injectSubmenu(RENUMBER_TITLE, AppIconLoader.Sort.getIcon(), EDIT_TITLE);
        swingUI.injectMenuAction(getRenumberXAxisAction(), RENUMBER_TITLE);
        swingUI.injectMenuAction(getRenumberYAxisAction(), RENUMBER_TITLE);
        swingUI.injectSubmenu(EXPAND_TITLE, AppIconLoader.BranchAdd.getIcon(), EDIT_TITLE);
        swingUI.injectMenuAction(getExpandSelectionAllAction(), EXPAND_TITLE);
        swingUI.injectMenuAction(getExpandSelectionImmediateAction(), EXPAND_TITLE);
        swingUI.injectMenuAction(getExpandSelectionSameTypeAction(), EXPAND_TITLE);
        swingUI.injectMenuAction(null, EDIT_TITLE);
        swingUI.injectMenuAction(actionFactory.createEditProjectAction(plugInPort), EDIT_TITLE);

        refreshActions();
    }

    @Override
    public EnumSet<EventType> getSubscribedEventTypes() {
        return EnumSet.of(EventType.SELECTION_CHANGED, EventType.PROJECT_MODIFIED);
    }

    @Override
    public void processMessage(EventType eventType, Object... params) {
        switch (eventType) {
        case SELECTION_CHANGED:
            refreshActions();
            break;
        case PROJECT_MODIFIED:
            undoHandler.stateChanged((Project) params[0], (Project) params[1], (String) params[2]);
            break;
        default:
            break;
        }
    }

    private void refreshActions() {
        boolean enabled = !plugInPort.getSelectedComponents().isEmpty();
        getCutAction().setEnabled(enabled);
        getCopyAction().setEnabled(enabled);
        try {
            getPasteAction().setEnabled(clipboard.isDataFlavorAvailable(ComponentTransferable.listFlavor));
        } catch (Exception e) {
            getPasteAction().setEnabled(false);
        }
        getEditSelectionAction().setEnabled(enabled);
        getDeleteSelectionAction().setEnabled(enabled);
        getExpandSelectionAllAction().setEnabled(enabled);
        getExpandSelectionImmediateAction().setEnabled(enabled);
        getExpandSelectionSameTypeAction().setEnabled(enabled);
        getSaveAsTemplateAction().setEnabled(enabled);
    }

    // ClipboardOwner

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        refreshActions();
    }
}
