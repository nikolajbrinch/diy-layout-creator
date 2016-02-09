package org.diylc.app.model;

import java.nio.file.Path;
import java.util.List;

import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;

public interface Model {

    public List<IDIYComponent> getSelectedComponents();

    public void dispose();

    public void loadProject(Project project, boolean freshStart) throws Exception;

    public void loadProject(Path path) throws Exception;

}
