package org.diylc.app.view.bom;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.diylc.app.FileFilterEnum;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.core.BomEntry;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BomDialog extends JDialog {

	private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(BomDialog.class);
	
	private ObjectListTable<BomEntry> table;
	private JPanel toolbar;

	public BomDialog(JFrame parent, List<BomEntry> bom) {
		super(parent, "Bill of Materials");
		setContentPane(createMainPanel());
		getTable().setData(bom);
		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			getTable().autoFit(Arrays.asList(3));
		}
	}

	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(getToolbar(), BorderLayout.NORTH);
		mainPanel.add(new JScrollPane(getTable()), BorderLayout.CENTER);
		return mainPanel;
	}

	private ObjectListTable<BomEntry> getTable() {
		if (table == null) {
			try {
				table = new ObjectListTable<BomEntry>(BomEntry.class, new String[] { "getName",
						"getValue", "getQuantity", "getNotes/setNotes" }, null);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return table;
	}

	private JPanel getToolbar() {
		if (toolbar == null) {
			toolbar = new JPanel();
			toolbar.add(new JButton(new SaveToExcelAction()));
			toolbar.add(new JButton(new SaveToCSVAction()));
			toolbar.add(new JButton(new SaveToPNGAction()));
			toolbar.add(new JButton(new SaveToHTMLAction()));
		}
		return toolbar;
	}

	class SaveToExcelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveToExcelAction() {
			super();
			putValue(Action.NAME, "Save to Excel");
			putValue(Action.SMALL_ICON, AppIconLoader.Excel.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Path path = DialogFactory.getInstance().showSaveDialog(
					FileFilterEnum.EXCEL.getFilter(), Configuration.INSTANCE.getLastPath(), null,
					FileFilterEnum.EXCEL.getExtensions()[0], null);
			if (path != null) {
				try {
					TableExporter.getInstance().exportToExcel(getTable(), path);
				} catch (IOException e1) {
	                LOG.warn("Error exporting to Excel", e1);
				}
			}
		}
	}

	class SaveToCSVAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveToCSVAction() {
			super();
			putValue(Action.NAME, "Save to CSV");
			putValue(Action.SMALL_ICON, AppIconLoader.CSV.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.CSV.getFilter(),
			        Configuration.INSTANCE.getLastPath(), null, FileFilterEnum.CSV.getExtensions()[0], null);
			if (path != null) {
				try {
					TableExporter.getInstance().exportToCSV(getTable(), path);
				} catch (IOException e1) {
				    LOG.warn("Error exporting to CSV", e1);
				}
			}
		}
	}

	class SaveToHTMLAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveToHTMLAction() {
			super();
			putValue(Action.NAME, "Save to HTML");
			putValue(Action.SMALL_ICON, AppIconLoader.HTML.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.HTML.getFilter(),
			        Configuration.INSTANCE.getLastPath(), null, FileFilterEnum.HTML.getExtensions()[0], null);
			if (path != null) {
				try {
					TableExporter.getInstance().exportToHTML(getTable(), path);
				} catch (IOException e1) {
				    LOG.warn("Error exporting to HTML", e1);
				}
			}
		}
	}

	class SaveToPNGAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveToPNGAction() {
			super();
			putValue(Action.NAME, "Save to PNG");
			putValue(Action.SMALL_ICON, AppIconLoader.Image.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.PNG.getFilter(),
			        Configuration.INSTANCE.getLastPath(), null, FileFilterEnum.PNG.getExtensions()[0], null);
			if (path != null) {
				try {
					TableExporter.getInstance().exportToPNG(getTable(), path);
				} catch (IOException e1) {
				    LOG.warn("Error exporting to PNG", e1);
				}
			}
		}
	}
}
