package org.diylc.app.view.properties;

import java.util.Collection;
import java.util.List;

import javax.swing.SwingConstants;

import org.diylc.app.view.BadPositionException;
import org.diylc.app.view.IPlugIn;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.ISwingUI;
import org.diylc.core.IDIYComponent;
import org.diylc.core.config.Configuration;
import org.diylc.core.config.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nikolajbrinch@gmail.com
 */
public class PropertyPlugin implements IPlugIn {

  private static final Logger LOG = LoggerFactory.getLogger(PropertyPlugin.class);

  private final ISwingUI swingUI;

  private IPlugInPort plugInPort;

  private PropertyView propertyView;

  public PropertyPlugin(ISwingUI swingUI) {
    this.swingUI = swingUI;
  }

  @Override
  public void connect(IPlugInPort plugInPort) {
    this.plugInPort = plugInPort;
    this.propertyView = new PropertyView(new PropertyController(plugInPort));

    try {
      if (Configuration.INSTANCE.getPropertyPanel()) {
        swingUI.injectGUIComponent(getPropertyView(), SwingConstants.RIGHT);
      }
    } catch (BadPositionException e) {
      LOG.error("Could not install property plugin", e);
    }

    Configuration.INSTANCE.addListener(Configuration.Key.PROPERTY_PANEL,
        new ConfigurationListener() {

          @Override
          public void onValueChanged(Object oldValue, Object newValue) {
            try {
              if ((boolean) newValue) {
                swingUI.injectGUIComponent(getPropertyView(), SwingConstants.RIGHT);
              } else {
                swingUI.removeGUIComponent(SwingConstants.RIGHT);
              }
            } catch (BadPositionException e) {
              LOG.error("Could not toggle property plugin", e);
            }
          }
        });
  }

  private PropertyView getPropertyView() {
    return propertyView;
  }

  public void updateSelectionState(List<IDIYComponent> selection,
      Collection<IDIYComponent> stuckComponents) {
    getPropertyView().displayProperties(plugInPort.getMutualSelectionProperties());
  }
}
