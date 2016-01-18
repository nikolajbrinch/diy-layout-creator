package org.diylc.app.menus.file;

import java.nio.file.Path;
import java.util.EnumSet;

import org.diylc.app.EventType;
import org.diylc.app.IPlugIn;
import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.core.LRU;
import org.diylc.core.config.Configuration;

import com.sun.javafx.util.Utils;

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

		FileActionFactory actionFactory = FileActionFactory.INSTANCE;
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
		
		if (!Utils.isMac()) {
			swingUI.injectMenuAction(null, FILE_TITLE);
			swingUI.injectMenuAction(actionFactory.createExitAction(plugInPort), FILE_TITLE);
		}
	}

	@Override
	public EnumSet<EventType> getSubscribedEventTypes() {
		return EnumSet.of(EventType.LRU_UPDATED);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processMessage(EventType eventType, Object... params) {
		if (eventType == EventType.LRU_UPDATED) {
			updateLru((LRU<Path>) params[0]);
		}
	}
	
	private void updateLru(LRU<Path> lru) {
		swingUI.clearMenuItems(OPEN_RECENT_TITLE);
		FileActionFactory actionFactory = FileActionFactory.INSTANCE;
		
		for (Path path : lru.getItems()) {
			swingUI.injectMenuAction(actionFactory.createOpenRecentAction(plugInPort, swingUI, path), OPEN_RECENT_TITLE);
		}
		
		Configuration.INSTANCE.setLru(lru);
	}

}
