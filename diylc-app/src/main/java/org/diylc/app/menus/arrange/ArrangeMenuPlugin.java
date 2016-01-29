package org.diylc.app.menus.arrange;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.EnumSet;

import org.diylc.app.view.IPlugIn;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.ISwingUI;
import org.diylc.core.EventType;

public class ArrangeMenuPlugin implements IPlugIn, ClipboardOwner {

	private static final String ARRANGE_TITLE = "Arrange";

    private final ISwingUI swingUI;

    private IPlugInPort plugInPort;

	private ArrangeActionFactory.GroupAction groupAction;
	
	private ArrangeActionFactory.UngroupAction ungroupAction;
	
	private ArrangeActionFactory.SendToBackAction sendToBackAction;
	
	private ArrangeActionFactory.BringToFrontAction bringToFrontAction;
	
	private ArrangeActionFactory.RotateSelectionAction rotateClockwiseAction;
	
	private ArrangeActionFactory.RotateSelectionAction rotateCounterClockwiseAction;

	public ArrangeMenuPlugin(ISwingUI swingUI) {
		this.swingUI = swingUI;
	}

	public ArrangeActionFactory.GroupAction getGroupAction() {
		if (groupAction == null) {
			groupAction = ArrangeActionFactory.INSTANCE.createGroupAction(
					plugInPort);
		}
		return groupAction;
	}

	public ArrangeActionFactory.UngroupAction getUngroupAction() {
		if (ungroupAction == null) {
			ungroupAction = ArrangeActionFactory.INSTANCE.createUngroupAction(
					plugInPort);
		}
		return ungroupAction;
	}

	public ArrangeActionFactory.SendToBackAction getSendToBackAction() {
		if (sendToBackAction == null) {
			sendToBackAction = ArrangeActionFactory.INSTANCE.createSendToBackAction(plugInPort);
		}
		return sendToBackAction;
	}

	public ArrangeActionFactory.BringToFrontAction getBringToFrontAction() {
		if (bringToFrontAction == null) {
			bringToFrontAction = ArrangeActionFactory.INSTANCE
					.createBringToFrontAction(plugInPort);
		}
		return bringToFrontAction;
	}

	public ArrangeActionFactory.RotateSelectionAction getRotateClockwiseAction() {
		if (rotateClockwiseAction == null) {
			rotateClockwiseAction = ArrangeActionFactory.INSTANCE
					.createRotateSelectionAction(plugInPort, 1);
		}
		return rotateClockwiseAction;
	}

	public ArrangeActionFactory.RotateSelectionAction getRotateCounterclockwiseAction() {
		if (rotateCounterClockwiseAction == null) {
			rotateCounterClockwiseAction = ArrangeActionFactory.INSTANCE
					.createRotateSelectionAction(plugInPort, -1);
		}
		return rotateCounterClockwiseAction;
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		this.plugInPort = plugInPort;

		swingUI.injectMenuAction(getSendToBackAction(), ARRANGE_TITLE);
		swingUI.injectMenuAction(getBringToFrontAction(), ARRANGE_TITLE);
		swingUI.injectMenuAction(null, ARRANGE_TITLE);
		swingUI.injectMenuAction(getRotateClockwiseAction(), ARRANGE_TITLE);
		swingUI.injectMenuAction(getRotateCounterclockwiseAction(), ARRANGE_TITLE);		
		swingUI.injectMenuAction(null, ARRANGE_TITLE);
		swingUI.injectMenuAction(getGroupAction(), ARRANGE_TITLE);
		swingUI.injectMenuAction(getUngroupAction(), ARRANGE_TITLE);
		swingUI.injectMenuAction(null, ARRANGE_TITLE);

		refreshActions();
	}

	@Override
	public EnumSet<EventType> getSubscribedEventTypes() {
		return EnumSet.of(EventType.SELECTION_CHANGED,
				EventType.PROJECT_MODIFIED);
	}

	@Override
	public void processMessage(EventType eventType, Object... params) {
		switch (eventType) {
    		case SELECTION_CHANGED:
    			refreshActions();
    			break;
			default:
			    break;
		}
	}

	private void refreshActions() {
		boolean enabled = !plugInPort.getSelectedComponents().isEmpty();
		getGroupAction().setEnabled(enabled);
		getUngroupAction().setEnabled(enabled);
		getSendToBackAction().setEnabled(enabled);
		getBringToFrontAction().setEnabled(enabled);
		getRotateClockwiseAction().setEnabled(enabled);
		getRotateCounterclockwiseAction().setEnabled(enabled);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		refreshActions();
	}
}
