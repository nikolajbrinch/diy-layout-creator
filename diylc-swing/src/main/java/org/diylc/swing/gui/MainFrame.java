package org.diylc.swing.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.diylc.appframework.miscutils.ConfigurationManager;
import org.diylc.common.BadPositionException;
import org.diylc.common.EventType;
import org.diylc.common.IPlugIn;
import org.diylc.common.IPlugInPort;
import org.diylc.common.ITask;
import org.diylc.common.PropertyWrapper;
import org.diylc.core.IView;
import org.diylc.images.CoreIconLoader;
import org.diylc.presenter.Presenter;
import org.diylc.swing.ISwingUI;
import org.diylc.swing.gui.editor.PropertyEditorDialog;
import org.diylc.swing.plugins.autosave.AutoSavePlugin;
import org.diylc.swing.plugins.canvas.CanvasPlugin;
import org.diylc.swing.plugins.config.ConfigPlugin;
import org.diylc.swing.plugins.edit.EditMenuPlugin;
import org.diylc.swing.plugins.file.FileFilterEnum;
import org.diylc.swing.plugins.file.FileMenuPlugin;
import org.diylc.swing.plugins.help.HelpMenuPlugin;
import org.diylc.swing.plugins.layers.LayersMenuPlugin;
import org.diylc.swing.plugins.statusbar.StatusBar;
import org.diylc.swing.plugins.toolbox.ToolBox;
import org.diylc.swingframework.ButtonDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainFrame extends JFrame implements ISwingUI {

	private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);

	private static final long serialVersionUID = 1L;

	private JPanel centerPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel topPanel;
	private JPanel bottomPanel;

	private Presenter presenter;

	private JMenuBar mainMenuBar;
	private Map<String, JMenu> menuMap;
	private Map<String, ButtonGroup> buttonGroupMap;

	private CanvasPlugin canvasPlugin;

	public MainFrame() {
		super("DIYLC 4");
		initComponent();
	}
	
	private void initComponent() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		Map<String, Integer> windowBounds = loadWindowBounds();
		setPreferredSize(new Dimension(windowBounds.get("width"), windowBounds.get("height")));
		createBasePanels();
		menuMap = new HashMap<String, JMenu>();
		buttonGroupMap = new HashMap<String, ButtonGroup>();
		setIconImages(Arrays.asList(CoreIconLoader.IconSmall.getImage(), CoreIconLoader.IconMedium
				.getImage(), CoreIconLoader.IconLarge.getImage()));
		DialogFactory.getInstance().initialize(this);

		this.presenter = new Presenter(this);

		presenter.installPlugin(new ToolBox(this));
		presenter.installPlugin(new FileMenuPlugin(this));
		presenter.installPlugin(new EditMenuPlugin(this));
		presenter.installPlugin(new ConfigPlugin(this));
		presenter.installPlugin(new LayersMenuPlugin(this));
		// presenter.installPlugin(new OnlineManager());
		presenter.installPlugin(new HelpMenuPlugin(this));

		presenter.installPlugin(new StatusBar(this));
		canvasPlugin = new CanvasPlugin(this);
		presenter.installPlugin(canvasPlugin);
		presenter.installPlugin(new FramePlugin());
		presenter.configure();

		try {
			File testFile = new File("test.tmp");
			Writer out = new OutputStreamWriter(new FileOutputStream(testFile));
			out.write("This is a test");
			out.close();
			testFile.delete();
			presenter.installPlugin(new AutoSavePlugin(this));
		} catch (Exception e) {
			showMessage(
					"The current user does not have permissions to access folder "
							+ new File(".").getAbsolutePath()
							+ ".\nAuto-save feature will not be available, contact your system administrator.",
					"Warning", IView.WARNING_MESSAGE);
		}

		presenter.createNewProject();

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				if (presenter.allowFileAction()) {
					ConfigurationManager.getInstance().writeValue(IPlugInPort.ABNORMAL_EXIT_KEY,
							false);
					dispose();
					presenter.dispose();
					System.exit(0);
				}
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (presenter.allowFileAction()) {
					ConfigurationManager.getInstance().writeValue(IPlugInPort.ABNORMAL_EXIT_KEY,
							false);
					dispose();
					presenter.dispose();
					System.exit(0);
				}
			}
		});

		setLocation(new Point(windowBounds.get("x"), windowBounds.get("y")));
		setExtendedState(windowBounds.get("extendedState"));
		
		addComponentListener(new ComponentAdapter() {
			@Override
            public void componentResized(ComponentEvent e) {
            	saveWindowBounds((JFrame) e.getComponent());
            }

			@Override
			public void componentMoved(ComponentEvent e) {
            	saveWindowBounds((JFrame) e.getComponent());
			}
            
        });

		setGlassPane(new CustomGlassPane());
	}

	private Map<String, Integer> loadWindowBounds() {
		Map<String, Integer> defaultWindowBounds = new HashMap<>();
		defaultWindowBounds.put("width", 800);
		defaultWindowBounds.put("height", 600);
		defaultWindowBounds.put("x", 100);
		defaultWindowBounds.put("y", 100);
		defaultWindowBounds.put("extendedState", JFrame.NORMAL);
		
		Map<String, Integer> windowBounds = (Map<String, Integer>) ConfigurationManager.getInstance().readObject("windowBounds", defaultWindowBounds);
		
		for (Map.Entry<String, Integer> entry : defaultWindowBounds.entrySet()) {
			if (windowBounds.get(entry.getKey()) == null) {
				windowBounds.put(entry.getKey(), entry.getValue());
			}
		}
		
		return windowBounds;
	}

	private void saveWindowBounds(JFrame frame) {
		Map<String, Integer> windowBounds = new HashMap<>();
		windowBounds.put("width", frame.getSize().width);
		windowBounds.put("height", frame.getSize().height);
		windowBounds.put("x", frame.getLocation().x);
		windowBounds.put("y", frame.getLocation().y);
		windowBounds.put("extendedState", frame.getExtendedState());

		ConfigurationManager.getInstance().writeValue("windowBounds", windowBounds);
	}

	public Presenter getPresenter() {
		return presenter;
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		// TODO: hack to prevent painting issues in the scroll bar rulers. Find
		// a better fix if possible.
		Timer timer = new Timer(500, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				canvasPlugin.refresh();
			}

		});
		timer.setRepeats(false);
		timer.start();
		// if (b) {
		// SwingUtilities.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		//					
		// }
		// });
		// }
	}

	private void createBasePanels() {
		Container c = new Container();
		c.setLayout(new BorderLayout());

		centerPanel = new JPanel(new BorderLayout());
		c.add(centerPanel, BorderLayout.CENTER);

		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		c.add(topPanel, BorderLayout.NORTH);

		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
		c.add(leftPanel, BorderLayout.WEST);

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		c.add(bottomPanel, BorderLayout.SOUTH);

		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
		c.add(rightPanel, BorderLayout.EAST);

		setContentPane(c);
	}

	private JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new JMenuBar();
			setJMenuBar(mainMenuBar);
		}
		return mainMenuBar;
	}

	// IView

	@Override
	public void showMessage(String message, String title, int messageType) {
		JOptionPane.showMessageDialog(this, message, title, messageType);
	}

	@Override
	public int showConfirmDialog(String message, String title, int optionType, int messageType) {
		return JOptionPane.showConfirmDialog(this, message, title, optionType, messageType);
	}

	@Override
	public boolean editProperties(List<PropertyWrapper> properties,
			Set<PropertyWrapper> defaultedProperties) {
		PropertyEditorDialog editor = DialogFactory.getInstance().createPropertyEditorDialog(
				properties, "Edit Selection");
		editor.setVisible(true);
		defaultedProperties.addAll(editor.getDefaultedProperties());
		return ButtonDialog.OK.equals(editor.getSelectedButtonCaption());
	}

	private JMenu findOrCreateMenu(String menuName) {
		JMenu menu = findMenu(menuName);
		
		if (menu == null) {
			menu = new JMenu(menuName);
			menuMap.put(menuName, menu);
			getMainMenuBar().add(menu);
		}
		
		return menu;
	}

	private JMenu findMenu(String menuName) {
		JMenu menu = null;
		
		if (menuMap.containsKey(menuName)) {
			menu = menuMap.get(menuName);
		}

		return menu;
	}

	
	@Override
	public void injectGUIComponent(JComponent component, int position) throws BadPositionException {
		LOG.info(String.format("injectGUIComponent(%s, %s)", component.getClass().getName(),
				position));
		switch (position) {
		case SwingConstants.TOP:
			topPanel.add(component);
			break;
		case SwingConstants.LEFT:
			leftPanel.add(component);
			break;
		case SwingConstants.BOTTOM:
			bottomPanel.add(component);
			break;
		case SwingConstants.RIGHT:
			rightPanel.add(component);
			break;
		case SwingConstants.CENTER:
			centerPanel.add(component, BorderLayout.CENTER);
			break;
		default:
			throw new BadPositionException();
		}
		pack();
	}

	@Override
	public void injectMenuAction(Action action, String menuName) {
		LOG.info(String.format("injectMenuAction(%s, %s)", action == null ? "Separator" : action
				.getValue(Action.NAME), menuName));
		JMenu menu = findOrCreateMenu(menuName);
		if (action == null) {
			menu.addSeparator();
		} else {
			Boolean isCheckBox = (Boolean) action.getValue(IView.CHECK_BOX_MENU_ITEM);
			String groupName = (String) action.getValue(IView.RADIO_BUTTON_GROUP_KEY);
			if (isCheckBox != null && isCheckBox) {
				menu.add(new JCheckBoxMenuItem(action));
			} else if (groupName != null) {
				ButtonGroup group;
				if (buttonGroupMap.containsKey(groupName)) {
					group = buttonGroupMap.get(groupName);
				} else {
					group = new ButtonGroup();
					buttonGroupMap.put(groupName, group);
				}
				JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
				group.add(item);
				menu.add(item);
			} else {
				menu.add(action);
			}
		}
	}

	@Override
	public void removeMenuAction(Action action, String menuName) {
		LOG.info(String.format("removeMenuAction(%s, %s)", action == null ? "Separator" : action
				.getValue(Action.NAME), menuName));
		JMenu menu = findMenu(menuName);
		
		if (menu != null) {
			int itemCount = menu.getItemCount();
			for (int i = 0; i < itemCount; i++) {
				JMenuItem item = menu.getItem(i);
				if (item.getAction() == action) {
					menu.remove(i);
					break;
				}
			}
		}
	}

	@Override
	public void clearMenuItems(String menuName) {
		LOG.info(String.format("clearMenuItems(%s)", menuName));
		JMenu menu = findMenu(menuName);
		menu.removeAll();
	}

	@Override
	public void injectSubmenu(String name, Icon icon, String parentMenuName) {
		LOG.info(String.format("injectSubmenu(%s, icon, %s)", name, parentMenuName));
		JMenu menu = findOrCreateMenu(parentMenuName);
		JMenu submenu = new JMenu(name);
		submenu.setIcon(icon);
		menu.add(submenu);
		menuMap.put(name, submenu);
	}
	
	@Override
	public <T extends Object> void executeBackgroundTask(final ITask<T> task) {
		getGlassPane().setVisible(true);
		SwingWorker<T, Void> worker = new SwingWorker<T, Void>() {

			@Override
			protected T doInBackground() throws Exception {
				return task.doInBackground();
			}

			@Override
			protected void done() {
				try {
					T result = get();
					task.complete(result);
				} catch (Exception e) {
					LOG.error("Task failed", e);
					task.failed(e);
				}
				getGlassPane().setVisible(false);
			}
		};
		worker.execute();
	}
	
	@Override
	public File promptFileSave() {
		return DialogFactory.getInstance().showSaveDialog(
				FileFilterEnum.DIY.getFilter(), null,
				FileFilterEnum.DIY.getExtensions()[0], null);
	}

	class FramePlugin implements IPlugIn {

		private IPlugInPort plugInPort;

		@Override
		public void connect(IPlugInPort plugInPort) {
			this.plugInPort = plugInPort;
		}

		@Override
		public EnumSet<EventType> getSubscribedEventTypes() {
			return EnumSet.of(EventType.FILE_STATUS_CHANGED);
		}

		@Override
		public void processMessage(EventType eventType, Object... params) {
			if (eventType == EventType.FILE_STATUS_CHANGED) {
				String fileName = (String) params[0];
				if (fileName == null) {
					fileName = "Untitled";
				}
				String modified = (Boolean) params[1] ? " (modified)" : "";
				setTitle(String.format("DIYLC %s - %s %s", plugInPort
						.getCurrentVersionNumber(), fileName, modified));
			}
		}
	}

}
