package org.diylc.app.view.menus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.diylc.app.Accelerators;
import org.diylc.app.actions.CheckBoxAction;
import org.diylc.app.actions.RadioButtonAction;
import org.diylc.app.controllers.ViewController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author nikolajbrinch@gmail.com
 */
public class ViewMenuPlugin extends AbstractMenuPlugin<ViewController> {

    private static final Logger LOG = LoggerFactory.getLogger(ViewMenuPlugin.class);

    public ViewMenuPlugin(ViewController viewController, View view, DrawingModel model) {
        super(viewController, view, model);
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        addMenuAction(new CheckBoxAction("Anti-Aliasing", Configuration.INSTANCE.getAntiAliasing(), (event) -> getController()
                .antiAliasing(event)), MenuConstants.VIEW_MENU);
        addMenuAction(new CheckBoxAction("Hi-Quality Rendering", Configuration.INSTANCE.getHiQualityRender(), (event) -> getController()
                .hiQualityRendering(event)), MenuConstants.VIEW_MENU);
        addMenuSeparator(MenuConstants.VIEW_MENU);
        addMenuAction(
                new CheckBoxAction("Outline Mode", Configuration.INSTANCE.getOutline(), (event) -> getController().outlineMode(event)),
                MenuConstants.VIEW_MENU);
        addMenuAction(new CheckBoxAction("Mouse Wheel Zoom", Configuration.INSTANCE.getWheelZoom(), (event) -> getController()
                .mouseWheelZoom(event)), MenuConstants.VIEW_MENU);
        addMenuSeparator(MenuConstants.VIEW_MENU);
        addMenuAction(new CheckBoxAction("Property Panel", Accelerators.SHOW_PROPERTY_PANEL, Configuration.INSTANCE.getPropertyPanel(), (
                event) -> getController().propertyPanel(event)), MenuConstants.VIEW_MENU);

        File themeDir = new File("themes");

        if (themeDir.exists()) {
            XStream xStream = new XStream(new DomDriver());

            addSubmenu(MenuConstants.VIEW_THEME_MENU, AppIconLoader.Pens.getIcon(), MenuConstants.VIEW_MENU);

            for (File file : themeDir.listFiles()) {
                if (file.getName().toLowerCase().endsWith(".xml")) {
                    try {
                        InputStream in = new FileInputStream(file);
                        Theme theme = (Theme) xStream.fromXML(in);
                        LOG.debug("Found theme: " + theme.getName());
                        addMenuAction(
                                new RadioButtonAction(theme.getName(), "theme", (event) -> getController().switchTheme(theme)), MenuConstants.VIEW_THEME_MENU);
                    } catch (Exception e) {
                        LOG.error("Could not load theme file " + file.getName(), e);
                    }
                }
            }
        }
    }

}
