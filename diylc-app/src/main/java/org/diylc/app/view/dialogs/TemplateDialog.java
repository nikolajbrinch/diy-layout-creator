package org.diylc.app.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.diylc.app.model.Model;
import org.diylc.app.view.Presenter;
import org.diylc.app.view.rendering.DrawingOption;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(TemplateDialog.class);

	private static final Dimension panelSize = new Dimension(400, 300);

	private Model model;

	private JList<Path> fileList;
	
	private JPanel canvasPanel;
	
	private Presenter presenter;
	
	private List<Path> files;
	
	private JCheckBox showTemplatesBox;
	
	private JPanel mainPanel;
	
	private JButton loadButton;
	
	private JPanel infoPanel;
	

	public TemplateDialog(JFrame owner, Model model) {
		super(owner, "Templates");
		setModal(true);
		setResizable(false);
		this.model = model;
		this.presenter = new Presenter();
		setContentPane(getMainPanel());
		pack();
		setLocationRelativeTo(owner);
	}

	public JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new GridBagLayout());
			mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 0;
			c.weighty = 1;
			c.weightx = 1;
			c.gridwidth = 2;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(0, 0, 4, 0);
			mainPanel.add(getInfoPanel(), c);

			c.gridy = 1;
			c.weightx = 0;
			c.gridwidth = 1;
			c.insets = new Insets(0, 0, 0, 0);
			JScrollPane scrollPane = new JScrollPane(getFileList());
			mainPanel.add(scrollPane, c);

			c.gridx = 1;
			c.weightx = 1;

			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(scrollPane.getBorder());
			panel.add(getCanvasPanel(), BorderLayout.CENTER);
			mainPanel.add(panel, c);

			c.gridx = 0;
			c.gridy = 2;
			c.weightx = 0;
			c.weighty = 0;
			c.anchor = GridBagConstraints.LAST_LINE_START;
			c.fill = GridBagConstraints.NONE;
			mainPanel.add(getShowTemplatesBox(), c);

			c.gridx = 1;
			c.anchor = GridBagConstraints.LAST_LINE_END;
			c.insets = new Insets(4, 0, 0, 0);
			mainPanel.add(getLoadButton(), c);
		}
		return mainPanel;
	}

	public JButton getLoadButton() {
		if (loadButton == null) {
			loadButton = new JButton("Load Template");
			loadButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					try {
                        model.loadProject(model.getProject(), true);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
					dispose();
				}
			});
		}
		return loadButton;
	}

	public JPanel getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			infoPanel.setBackground(Color.decode("#FFFFCC"));
			infoPanel.setBorder(new JTextField().getBorder());
			JLabel infoLabel = new JLabel(
					"<html>Templates are semi-product layouts that are used as a starting point for your projects.<br>"
							+ "Pick a templete in the left list and click \"Load Template\" to load it or close this dialog to continue.<br>"
							+ "You can create your own templates by placing a DIY file into <b>diylc/templates</b> directory.</html>");
			infoLabel.setOpaque(false);
			infoPanel.add(infoLabel);
		}
		return infoPanel;
	}

	public JList<Path> getFileList() {
		if (fileList == null) {
			fileList = new JList<Path>(getFiles().toArray(new Path[0]));
			fileList.setPreferredSize(new Dimension(128, -1));
			fileList.setCellRenderer(new DefaultListCellRenderer() {

				private static final long serialVersionUID = 1L;

				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,
							isSelected, cellHasFocus);
					if (value instanceof Path) {
						label.setText(((Path) value).getFileName().toString());
					}
					
					return label;
				}

			});
			fileList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					Path path = (Path) fileList.getSelectedValue();
					if (path != null) {
						try {
                            presenter.loadProjectFromFile(path);
                        } catch (Exception e1) {
                            throw new RuntimeException(e1);
                        }
						Dimension dim = presenter.getCanvasDimensions(true);
						double xFactor = panelSize.getWidth() / dim.getWidth();
						double yFactor = panelSize.getHeight() / dim.getHeight();
						presenter.setZoomLevel(Math.min(xFactor, yFactor));
						getCanvasPanel().repaint();
					}
				}
			});
			fileList.setSelectedIndex(0);
		}
		return fileList;
	}

	public JCheckBox getShowTemplatesBox() {
		if (showTemplatesBox == null) {
			showTemplatesBox = new JCheckBox("Show this dialog at startup");
			showTemplatesBox.setSelected(true);
			showTemplatesBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Configuration.INSTANCE.setShowTemplates(showTemplatesBox.isSelected());
				}
			});
		}
		return showTemplatesBox;
	}

	public List<Path> getFiles() {
		if (files == null) {
			files = new ArrayList<Path>();
			Path dir = Paths.get("templates");
			if (Files.exists(dir)) {
			    try (Stream<Path> stream = Files.list(dir)) {
			    files.addAll(stream
			            .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().toLowerCase().endsWith(".diy"))
			            .collect(Collectors.toList()));
			    } catch (IOException e) {
                    LOG.warn("Error getting template files", e);
                }
			}
			LOG.debug("Found " + files.size() + " templates");
		}
		return files;
	}

	public JPanel getCanvasPanel() {
		if (canvasPanel == null) {
			canvasPanel = new JPanel() {

				private static final long serialVersionUID = 1L;

				@Override
				public void paint(Graphics g) {
					super.paint(g);
					// Graphics2D g2d = (Graphics2D) g;
					// AffineTransform transform = g2d.getTransform();
					presenter.draw((Graphics2D) g, EnumSet.of(DrawingOption.ZOOM,
							DrawingOption.ANTIALIASING), null);
					// g2d.setTransform(transform);
					// Rectangle rect = canvasPanel.getBounds();
					// BorderFactory.createEtchedBorder().paintBorder(canvasPanel,
					// g, 0, 0,
					// (int) rect.getWidth(), (int) rect.getHeight());
				}
			};
			canvasPanel.setBackground(Color.white);
			canvasPanel.setPreferredSize(panelSize);
		}
		return canvasPanel;
	}
}
