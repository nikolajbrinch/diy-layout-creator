package org.diylc.app.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.EnumSet;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.diylc.app.view.rendering.DrawingOption;
import org.diylc.core.Project;
import org.diylc.core.platform.IFileChooserAccessory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * {@link JComponent} that shows preview of the selected project in {@link JFileChooser}. It's
 * hooked onto {@link JFileChooser} as {@link PropertyChangeListener} and refreshes when file is
 * selected.
 * 
 * @author Branislav Stojkovic
 */
public class ProjectPreview extends JPanel
    implements PropertyChangeListener, IFileChooserAccessory {

  private static final long serialVersionUID = 1L;

  private IPlugInPort presenter;
  private XStream xStream;
  private Project emptyProject;
  private RenderComponent renderComponent;
  private JLabel nameLabel;

  public ProjectPreview() {
    super();

    setPreferredSize(new Dimension(140, 128));
    presenter = new StubPresenter();
    xStream = new XStream(new DomDriver());

    emptyProject = new Project();
    emptyProject.setTitle("");

    renderComponent = new RenderComponent();
    nameLabel = new JLabel();

    add(renderComponent);
    add(nameLabel);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    boolean update = false;
    String prop = evt.getPropertyName();

    Project selectedProject = emptyProject;
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      update = true;
    } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
      File selectedFile = (File) evt.getNewValue();
      try {
        FileInputStream in = new FileInputStream(selectedFile);
        selectedProject = (Project) xStream.fromXML(in);
        in.close();
      } catch (Exception e) {
      }
      update = true;
    }

    nameLabel.setText(selectedProject.getTitle());
    presenter.loadProject(selectedProject, true);

    if (update) {
      if (renderComponent.isShowing()) {
        renderComponent.repaint();
      }
    }
  }

  class RenderComponent extends JComponent {

    private static final long serialVersionUID = 1L;

    public RenderComponent() {
      setPreferredSize(new Dimension(128, 96));
    }

    @Override
    public void paint(Graphics g) {
      super.paint(g);

      Graphics2D g2d = (Graphics2D) g;
      Dimension d = presenter.getCanvasDimensions(false);
      Rectangle rect = getBounds();
      double projectRatio = d.getWidth() / d.getHeight();
      double actualRatio = rect.getWidth() / rect.getHeight();
      double zoomRatio;
      if (projectRatio > actualRatio) {
        zoomRatio = rect.getWidth() / d.getWidth();
      } else {
        zoomRatio = rect.getHeight() / d.getHeight();
      }
      g2d.scale(zoomRatio, zoomRatio);
      presenter.draw(g2d, EnumSet.noneOf(DrawingOption.class), null);

      g2d.setColor(Color.black);
      g2d.drawRect(0, 0, d.width - (int) (1 / zoomRatio), d.height - (int) (1 / zoomRatio));
    }
  }

  @Override
  public void install(JFileChooser fileChooser) {
    fileChooser.setAccessory(this);
    fileChooser.addPropertyChangeListener(this);
  }
}
