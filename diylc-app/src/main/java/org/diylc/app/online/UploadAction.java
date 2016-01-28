package org.diylc.app.online;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.online.view.UploadDialog;
import org.diylc.app.utils.AppIconLoader;

class UploadAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public UploadAction() {
		super();
		putValue(AbstractAction.NAME, "Upload Project");
		putValue(AbstractAction.SMALL_ICON, AppIconLoader.Upload.getIcon());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		UploadDialog dialog = DialogFactory.getInstance().createUploadDialog();
		dialog.setVisible(true);
	}
}