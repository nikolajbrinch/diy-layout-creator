package org.diylc.app.model;

import java.nio.file.Path;
import java.util.List;

import org.diylc.app.view.Presenter;
import org.diylc.core.IDIYComponent;
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

    @Override
    public void loadProject(Path path) throws Exception {
        getPresenter().loadProjectFromFile(path);        
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
