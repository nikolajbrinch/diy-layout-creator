package org.diylc.app.online;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.online.view.LoginDialog;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.dialogs.DialogFactory;

class LoginAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public LoginAction() {
		super();
		putValue(AbstractAction.NAME, "Log in");
		putValue(AbstractAction.SMALL_ICON, AppIconLoader.IdCard.getIcon());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LoginDialog dialog = DialogFactory.getInstance().createLoginDialog();
		dialog.setVisible(true);
	}
}