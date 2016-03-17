package org.diylc.app.view.menus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.diylc.app.Accelerators;
import org.diylc.app.actions.CheckBoxAction;
import org.diylc.app.actions.RadioButtonAction;
import org.diylc.app.controllers.ViewController;
import org.diylc.app.model.Model;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
import org.diylc.core.resources.Resource;
import org.diylc.core.resources.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author nikolajbrinch@gmail.com
 */
public class ViewMenuPlugin extends AbstractMenuPlugin<ViewController> {

    private static final Logger LOG = LoggerFactory.getLogger(ViewMenuPlugin.class);

    private final ResourceLoader resourceLoader = new ResourceLoader();

    public ViewMenuPlugin(ViewController viewController, View view, Model model) {
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

        try {
            Set<Resource> resources = resourceLoader.getResources("themes", ".xml");

            XStream xStream = new XStream(new DomDriver());

            addSubmenu(MenuConstants.VIEW_THEME_MENU, AppIconLoader.Pens.getIcon(), MenuConstants.VIEW_MENU);

            for (Resource resource : resources) {
                if (resource.getFilename().toLowerCase().endsWith(".xml")) {
                    try (InputStream inputStream = resource.openStream()) {
                        Theme theme = (Theme) xStream.fromXML(inputStream);
                        LOG.debug("Found theme: " + theme.getName());
                        addMenuAction(new RadioButtonAction(theme.getName(), "theme", (event) -> getController().switchTheme(theme)),
                                MenuConstants.VIEW_THEME_MENU);
                    } catch (Exception e) {
                        LOG.error("Could not load theme file " + resource.toString(), e);
                    }
                }
            }
        } catch (IOException e) {
            LOG.warn("Could not load themes", e);
        }
    }

}
