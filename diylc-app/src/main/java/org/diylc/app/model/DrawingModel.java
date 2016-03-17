package org.diylc.app.model;

import java.util.List;

import org.diylc.app.view.Presenter;
import org.diylc.core.components.IDIYComponent;
import org.diylc.core.Project;

public class DrawingModel implements Model {

    private Presenter presenter;

    public DrawingModel() {
    }
    
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
        getPresenter().loadProject(project, freshStart);
    }

    public void pasteComponents(List<IDIYComponent> components) {
        getPresenter().pasteComponents(components);
    }

    public Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
