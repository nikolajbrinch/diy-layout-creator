package org.diylc.swing.plugins.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.diylc.appframework.miscutils.ConfigurationManager;
import org.diylc.appframework.miscutils.Environment;
import org.diylc.appframework.miscutils.OsType;
import org.diylc.common.EventType;
import org.diylc.common.IPlugIn;
import org.diylc.common.IPlugInPort;
import org.diylc.swing.ActionFactory;
import org.diylc.swing.ISwingUI;

/**
 * Entry point class for File management utilities.
 * 
 * @author Branislav Stojkovic
 */
public class FileMenuPlugin implements IPlugIn {

	private static final String FILE_TITLE = "File";
	private static final String OPEN_RECENT_TITLE = "Open Recent";

	private ProjectDrawingProvider drawingProvider;

	private ISwingUI swingUI;
	private IPlugInPort plugInPort;

	public FileMenuPlugin(ISwingUI swingUI) {
		super();
		this.swingUI = swingUI;
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		this.plugInPort = plugInPort;
		this.drawingProvider = new ProjectDrawingProvider(plugInPort, false, true);

		ActionFactory actionFactory = ActionFactory.getInstance();
		swingUI.injectMenuAction(actionFactory.createNewAction(plugInPort), FILE_TITLE);
		swingUI.injectMenuAction(actionFactory.createOpenAction(plugInPort, swingUI), FILE_TITLE);
		swingUI.injectSubmenu(OPEN_RECENT_TITLE, null, FILE_TITLE);
		swingUI.injectMenuAction(actionFactory.createImportAction(plugInPort, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(actionFactory.createSaveAction(plugInPort, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(actionFactory.createSaveAsAction(plugInPort, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(null, FILE_TITLE);
		swingUI.injectMenuAction(actionFactory.createExportPDFAction(drawingProvider, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(actionFactory.createExportPNGAction(drawingProvider, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(null, FILE_TITLE);
		swingUI.injectMenuAction(actionFactory.createPrintAction(drawingProvider), FILE_TITLE);
		
		if (Environment.INSTANCE.getOsType() != OsType.OSX) {
			swingUI.injectMenuAction(null, FILE_TITLE);
			swingUI.injectMenuAction(actionFactory.createExitAction(plugInPort), FILE_TITLE);
		}
	}

	@Override
	public EnumSet<EventType> getSubscribedEventTypes() {
		return EnumSet.of(EventType.LRU_UPDATED);
	}

	@Override
	public void processMessage(EventType eventType, Object... params) {
		if (eventType == EventType.LRU_UPDATED) {
			updateLru((List<Path>) params[0]);
		}
	}
	
	private void updateLru(List<Path> paths) {
		swingUI.clearMenuItems(OPEN_RECENT_TITLE);
		ActionFactory actionFactory = ActionFactory.getInstance();
		
		List<String> configLru = new ArrayList<>();
		
		for (Path path : paths) {
			swingUI.injectMenuAction(actionFactory.createOpenRecentAction(plugInPort, swingUI, path), OPEN_RECENT_TITLE);
			configLru.add(path.toAbsolutePath().normalize().toString());
		}
		
		ConfigurationManager.getInstance().writeValue("lru", configLru);
	}

}
