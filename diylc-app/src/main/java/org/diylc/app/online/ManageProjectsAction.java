package org.diylc.app.online;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.app.view.dialogs.ProgressDialog;

class ManageProjectsAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public ManageProjectsAction() {
		super();
		putValue(AbstractAction.NAME, "Manage Projects");
		putValue(AbstractAction.SMALL_ICON, AppIconLoader.Wrench.getIcon());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ProgressDialog dialog = DialogFactory.getInstance().createProgressDialog("Test",
				new String[] {}, "Some text", false);
		dialog.setVisible(true);
	}
}