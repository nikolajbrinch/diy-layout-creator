package org.diylc.swing.plugins.arrange;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.EnumSet;

import org.diylc.presenter.plugin.EventType;
import org.diylc.presenter.plugin.IPlugIn;
import org.diylc.presenter.plugin.IPlugInPort;
import org.diylc.swing.ActionFactory;
import org.diylc.swing.ISwingUI;

public class ArrangeMenuPlugin implements IPlugIn, ClipboardOwner {

	private static final String ARRANGE_TITLE = "Arrange";

	private IPlugInPort plugInPort;
	private ISwingUI swingUI;

	private ActionFactory.GroupAction groupAction;
	private ActionFactory.UngroupAction ungroupAction;
	private ActionFactory.SendToBackAction sendToBackAction;
	private ActionFactory.BringToFrontAction bringToFrontAction;
	private ActionFactory.RotateSelectionAction rotateClockwiseAction;
	private ActionFactory.RotateSelectionAction rotateCounterClockwiseAction;

	public ArrangeMenuPlugin(ISwingUI swingUI) {
		this.swingUI = swingUI;
		// SecurityManager securityManager = System.getSecurityManager();
		// if (securityManager != null) {
		// try {
		// securityManager.checkSystemClipboardAccess();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}

	public ActionFactory.GroupAction getGroupAction() {
		if (groupAction == null) {
			groupAction = ActionFactory.INSTANCE.createGroupAction(
					plugInPort);
		}
		return groupAction;
	}

	public ActionFactory.UngroupAction getUngroupAction() {
		if (ungroupAction == null) {
			ungroupAction = ActionFactory.INSTANCE.createUngroupAction(
					plugInPort);
		}
		return ungroupAction;
	}

	public ActionFactory.SendToBackAction getSendToBackAction() {
		if (sendToBackAction == null) {
			sendToBackAction = ActionFactory.INSTANCE.createSendToBackAction(plugInPort);
		}
		return sendToBackAction;
	}

	public ActionFactory.BringToFrontAction getBringToFrontAction() {
		if (bringToFrontAction == null) {
			bringToFrontAction = ActionFactory.INSTANCE
					.createBringToFrontAction(plugInPort);
		}
		return bringToFrontAction;
	}

	public ActionFactory.RotateSelectionAction getRotateClockwiseAction() {
		if (rotateClockwiseAction == null) {
			rotateClockwiseAction = ActionFactory.INSTANCE
					.createRotateSelectionAction(plugInPort, 1);
		}
		return rotateClockwiseAction;
	}

	public ActionFactory.RotateSelectionAction getRotateCounterclockwiseAction() {
		if (rotateCounterClockwiseAction == null) {
			rotateCounterClockwiseAction = ActionFactory.INSTANCE
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
