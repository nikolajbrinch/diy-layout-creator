package org.diylc.app.menus.file;

import java.nio.file.Path;
import java.util.EnumSet;

import org.diylc.app.actions.ExportPDFAction;
import org.diylc.app.actions.ExportPNGAction;
import org.diylc.app.actions.PrintAction;
import org.diylc.app.menus.file.actions.ExitAction;
import org.diylc.app.menus.file.actions.ImportAction;
import org.diylc.app.menus.file.actions.NewAction;
import org.diylc.app.menus.file.actions.OpenAction;
import org.diylc.app.menus.file.actions.OpenRecentAction;
import org.diylc.app.menus.file.actions.SaveAction;
import org.diylc.app.menus.file.actions.SaveAsAction;
import org.diylc.app.view.EventType;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.window.IPlugIn;
import org.diylc.app.window.ISwingUI;
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

		swingUI.injectMenuAction(new NewAction(plugInPort), FILE_TITLE);
		swingUI.injectMenuAction(new OpenAction(plugInPort, swingUI), FILE_TITLE);
		swingUI.injectSubmenu(OPEN_RECENT_TITLE, null, FILE_TITLE);
		swingUI.injectMenuAction(new ImportAction(plugInPort, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(new SaveAction(plugInPort, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(new SaveAsAction(plugInPort, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(null, FILE_TITLE);
		swingUI.injectMenuAction(new ExportPDFAction(drawingProvider, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(new ExportPNGAction(drawingProvider, swingUI), FILE_TITLE);
		swingUI.injectMenuAction(null, FILE_TITLE);
		swingUI.injectMenuAction(new PrintAction(drawingProvider), FILE_TITLE);
		
		if (!Utils.isMac()) {
			swingUI.injectMenuAction(null, FILE_TITLE);
			swingUI.injectMenuAction(new ExitAction(plugInPort), FILE_TITLE);
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
		
		for (Path path : lru.getItems()) {
			swingUI.injectMenuAction(new OpenRecentAction(plugInPort, swingUI, path), OPEN_RECENT_TITLE);
		}
		
		Configuration.INSTANCE.setLru(lru);
	}

}
