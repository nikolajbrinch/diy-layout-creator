package org.diylc.app.online;

import javax.swing.SwingWorker;

import org.diylc.app.online.presenter.LibraryPresenter;
import org.diylc.app.view.IPlugIn;
import org.diylc.app.view.IPlugInPort;

public class OnlineManager implements IPlugIn {

	private LibraryPresenter libraryPresenter;

	@Override
	public void connect(IPlugInPort plugInPort) {
		this.libraryPresenter = new LibraryPresenter();
		initialize();
	}

	private void initialize() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				libraryPresenter.connectDb();
				return null;
			}

			@Override
			protected void done() {
				if (libraryPresenter.isLoggedIn()) {

				}
			}
		};
		worker.execute();
	}
}
