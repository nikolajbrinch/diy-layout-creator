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
import org.diylc.components.registry.ComparatorFactory;
import org.diylc.components.registry.ComponentTypes;
import org.diylc.components.registry.Constants;
import org.diylc.core.ComponentType;
import org.diylc.core.Template;
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

    ComponentTypes componentTypes = plugInPort.getComponentRegistry().getComponentTypes();
    addTab("Recently Used", createRecentComponentsPanel());
    List<String> categories = componentTypes.getCategories();
    Collections.sort(categories);

    for (String category : categories) {
      JPanel panel = createTab((componentTypes.getComponents(category)));
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

  private JPanel createTab(List<ComponentType> componentTypes) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.add(createComponentPanel(componentTypes), BorderLayout.CENTER);

    return panel;
  }

  private Container getRecentToolbar() {
    if (recentToolbar == null) {
      recentToolbar = new Container();
      recentToolbar.setLayout(new BoxLayout(recentToolbar, BoxLayout.X_AXIS));
    }
    return recentToolbar;
  }

  private Component createComponentPanel(List<ComponentType> componentTypes) {
    Container toolbar = new Container();

    Collections.sort(componentTypes, ComparatorFactory.getInstance().getComponentTypeComparator());

    toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

    for (ComponentType componentType : componentTypes) {
      Component button = ComponentButtonFactory.create(plugInPort, componentType,
          createTemplatePopup(componentType));
      toolbar.add(button);
    }

    return toolbar;
  }

  private Component createRecentComponentsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    final Container toolbar = getRecentToolbar();
    refreshRecentComponentsToolbar(toolbar, Configuration.INSTANCE.getRecentComponents());

    Configuration.INSTANCE.addListener(Configuration.Key.RECENT_COMPONENTS,
        new ConfigurationListener() {
          @SuppressWarnings("unchecked")
          @Override
          public void onValueChanged(Object oldValue, Object newValue) {
            pendingRecentComponents = (List<String>) newValue;
          }
        });

    panel.add(toolbar, BorderLayout.CENTER);

    return panel;
  }

  private boolean refreshRecentComponentsToolbar(Container toolbar,
      List<String> recentComponentClassList) {
    toolbar.removeAll();

    boolean updateRecentComponentsConfiguraton = false;

    Iterator<String> iterator = recentComponentClassList.iterator();

    while (iterator.hasNext()) {
      String componentClassName = (String) iterator.next();
      ComponentType componentType =
          plugInPort.getComponentRegistry().getComponentType(componentClassName);
      if (componentType != null) {
        Component button = ComponentButtonFactory.create(plugInPort, componentType,
            createTemplatePopup(componentType));
        toolbar.add(button);
      } else {
        LOG.warn("Could not create recent component button for " + componentClassName);
        iterator.remove();
        updateRecentComponentsConfiguraton = true;
      }
    }

    return updateRecentComponentsConfiguraton;
  }

  private JPopupMenu createTemplatePopup(final ComponentType componentType) {
    final JPopupMenu templatePopup = new JPopupMenu();
    templatePopup.add("Loading...");
    templatePopup.addPopupMenuListener(new PopupMenuListener() {

      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        templatePopup.removeAll();
        List<Template> templates =
            plugInPort.getTemplatesFor(componentType.getCategory(), componentType.getName());
        if (templates == null || templates.isEmpty()) {
          JMenuItem item = new JMenuItem("<no templates>");
          item.setEnabled(false);
          templatePopup.add(item);
        } else {
          for (Template template : templates) {
            JMenuItem item =
                ComponentButtonFactory.createTemplateItem(plugInPort, template, componentType);
            templatePopup.add(item);
          }
        }
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {}
    });

    return templatePopup;
  }
}
