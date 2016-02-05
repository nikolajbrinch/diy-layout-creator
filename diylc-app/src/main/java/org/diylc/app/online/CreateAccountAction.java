package org.diylc.app.online;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.online.view.NewUserDialog;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.dialogs.DialogFactory;

class CreateAccountAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public CreateAccountAction() {
		super();
		putValue(AbstractAction.NAME, "Create New Account");
		putValue(AbstractAction.SMALL_ICON, AppIconLoader.IdCardAdd.getIcon());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		NewUserDialog dialog = DialogFactory.getInstance().createNewUserDialog();
		dialog.setVisible(true);
	}
}