package org.diylc.app.online;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.utils.AppIconLoader;

class LibraryAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public LibraryAction() {
		super();
		putValue(AbstractAction.NAME, "Browse Library");
		putValue(AbstractAction.SMALL_ICON, AppIconLoader.Chest.getIcon());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}