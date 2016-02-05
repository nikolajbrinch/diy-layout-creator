package org.diylc.app.model;

import java.nio.file.Path;
import java.util.List;

import org.diylc.app.view.Presenter;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;

public class DrawingModel implements Model {

    private final Presenter presenter;

    public DrawingModel(Presenter presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public List<IDIYComponent> getSelectedComponents() {
        return presenter.getSelectedComponents();
    }

    @Override
    public boolean allowFileAction() {
        return presenter.allowFileAction();
    }

    @Override
    public void dispose() {
        presenter.dispose();
    }

    @Override
    public void loadProject(Project project, boolean freshStart) {
        presenter.loadProject(project, freshStart);
    }

    @Override
    public void loadProject(Path path) throws Exception {
        presenter.loadProjectFromFile(path);        
    }

    public void pasteComponents(List<IDIYComponent> components) {
        presenter.pasteComponents(components);
    }

}
