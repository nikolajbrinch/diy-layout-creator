package org.diylc.app.model;

import java.util.List;

import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;

public interface Model {

  public List<IDIYComponent> getSelectedComponents();

  public void dispose();

  public void loadProject(Project project, boolean freshStart);

}
