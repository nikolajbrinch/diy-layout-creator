package org.diylc.app.update;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.diylc.appframework.update.Version;
import org.diylc.appframework.update.VersionNumber;



public class UpdateLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	private UpdateChecker updateChecker;
	private List<Version> updatedVersions;
	private Icon lightBulbOn;
	private Icon lightBulbOff;

	public UpdateLabel(final VersionNumber currentVersion, final String updateFileUrl, Icon lightBulbOn, Icon lightBulbOff) {
		super();
		this.lightBulbOn = lightBulbOn;
		this.lightBulbOff = lightBulbOff;
		updateChecker = new UpdateChecker(currentVersion, updateFileUrl);

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ((updatedVersions != null) && (updatedVersions.size() > 0)) {
					new UpdateDialog(UpdateLabel.this, updateChecker
							.createUpdateHTML(updatedVersions), updatedVersions.get(0).getUrl())
							.setVisible(true);
				} else {
					checkForUpdates();
				}
			}
		});
//		checkForUpdates();
	}

	private void checkForUpdates() {
		SwingWorker<List<Version>, Void> worker = new SwingWorker<List<Version>, Void>() {

			@Override
			protected List<Version> doInBackground() throws Exception {
				return updateChecker.findNewVersions();
			}

			@Override
			protected void done() {
				try {
					updatedVersions = get();
					if (updatedVersions.size() == 0) {
						setIcon(lightBulbOff);
						setToolTipText("No updates available, click to check again");
					} else {
						setIcon(lightBulbOn);
						setToolTipText("Updates are available, click to see details");
					}
				} catch (Exception e) {
					setIcon(lightBulbOff);
					setToolTipText("Error occured while searching for updates: " + e.getMessage());
					setCursor(Cursor.getDefaultCursor());
				}
			}
		};
		worker.execute();
	}
}
