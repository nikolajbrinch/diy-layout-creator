package org.diylc.app.model;

import java.util.List;

import org.diylc.app.view.Presenter;
import org.diylc.core.ComponentType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrawingModel implements Model {

  private Presenter presenter;

  public DrawingModel() {}

  @Override
  public List<IDIYComponent> getSelectedComponents() {
    return getPresenter().getSelectedComponents();
  }

  @Override
  public void dispose() {
    getPresenter().dispose();
  }

  @Override
  public void loadProject(Project project, boolean freshStart) {
    project.getComponents().forEach(component -> {
      ComponentType componentType =
          presenter.getComponentRegistry().getComponentType(component.getClass());
      component.setComponentType(componentType);
    });
    getPresenter().loadProject(project, freshStart);
  }

  public void pasteComponents(List<IDIYComponent> components) {
    getPresenter().pasteComponents(components);
  }

}
