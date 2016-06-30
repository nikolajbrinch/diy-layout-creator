package org.diylc.app.view.toolbox;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.diylc.app.view.IPlugInPort;
import org.diylc.core.ComparatorFactory;
import org.diylc.core.Template;
import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.registry.ComponentModels;
import org.diylc.core.components.registry.Constants;
import org.diylc.core.config.Configuration;
import org.diylc.core.config.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tabbed pane that shows all available components categorized into tabs.
 * 
 * @author Branislav Stojkovic
 */
class ComponentTabbedPane extends JTabbedPane {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTabbedPane.class);

    public static int SCROLL_STEP = Constants.ICON_SIZE + ComponentButtonFactory.MARGIN * 2 + 2;

    private final IPlugInPort plugInPort;

    private Container recentToolbar;

    private List<String> pendingRecentComponents = null;

    public ComponentTabbedPane(IPlugInPort plugInPort) {
        super();
        this.plugInPort = plugInPort;
        
        ComponentModels componentModels = plugInPort.getComponentRegistry().getComponentModels();
        addTab("Recently Used", createRecentComponentsPanel());
        List<String> categories = componentModels.getCategories();
        Collections.sort(categories);

        for (String category : categories) {
            JPanel panel = createTab((componentModels.getComponents(category)));
            addTab(category, panel);
        }

        addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                ComponentTabbedPane.this.plugInPort.setNewComponentTypeSlot(null, null);
                /*
                 * Refresh recent components if needed
                 */
                if (pendingRecentComponents != null) {
                    refreshRecentComponentsToolbar(getRecentToolbar(), pendingRecentComponents);
                    getRecentToolbar().invalidate();
                    pendingRecentComponents = null;
                }
            }
        });
    }

    private JPanel createTab(List<ComponentModel> componentModels) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(createComponentPanel(componentModels), BorderLayout.CENTER);

        return panel;
    }

    private Container getRecentToolbar() {
        if (recentToolbar == null) {
            recentToolbar = new Container();
            recentToolbar.setLayout(new BoxLayout(recentToolbar, BoxLayout.X_AXIS));
        }
        return recentToolbar;
    }

    private Component createComponentPanel(List<ComponentModel> componentModels) {
        Container toolbar = new Container();

        Collections.sort(componentModels, ComparatorFactory.getInstance().getComponentTypeComparator());

        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

        for (ComponentModel componentModel : componentModels) {
            Component button = ComponentButtonFactory.create(plugInPort, componentModel, createTemplatePopup(componentModel));
            toolbar.add(button);
        }

        return toolbar;
    }

    private Component createRecentComponentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        final Container toolbar = getRecentToolbar();
        refreshRecentComponentsToolbar(toolbar, Configuration.INSTANCE.getRecentComponents());

        Configuration.INSTANCE.addListener(Configuration.Key.RECENT_COMPONENTS, new ConfigurationListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onValueChanged(Object oldValue, Object newValue) {
                pendingRecentComponents = (List<String>) newValue;
            }
        });

        panel.add(toolbar, BorderLayout.CENTER);

        return panel;
    }

    private boolean refreshRecentComponentsToolbar(Container toolbar, List<String> recentComponents) {
        toolbar.removeAll();

        boolean updateRecentComponentsConfiguraton = false;

        Iterator<String> iterator = recentComponents.iterator();

        while (iterator.hasNext()) {
            String componentModelId = (String) iterator.next();
            ComponentModel componentModel = plugInPort.getComponentRegistry().getComponentModel(componentModelId);
            if (componentModel != null) {
                Component button = ComponentButtonFactory.create(plugInPort, componentModel, createTemplatePopup(componentModel));
                toolbar.add(button);
            } else {
                LOG.warn("Could not create recent component button for " + componentModelId);
                iterator.remove();
                updateRecentComponentsConfiguraton = true;
            }
        }

        return updateRecentComponentsConfiguraton;
    }

    private JPopupMenu createTemplatePopup(final ComponentModel componentModel) {
        final JPopupMenu templatePopup = new JPopupMenu();
        templatePopup.add("Loading...");
        templatePopup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                templatePopup.removeAll();
                List<Template> templates = plugInPort.getTemplatesFor(componentModel.getCategory(), componentModel.getName());
                if (templates == null || templates.isEmpty()) {
                    JMenuItem item = new JMenuItem("<no templates>");
                    item.setEnabled(false);
                    templatePopup.add(item);
                } else {
                    for (Template template : templates) {
                        JMenuItem item = ComponentButtonFactory.createTemplateItem(plugInPort, template, componentModel);
                        templatePopup.add(item);
                    }
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        return templatePopup;
    }
}
