package org.diylc.app.model;

import java.util.List;

import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.measures.Size;

public class Model {

    private Project project;

    public Model() {
        
    }
    
    public Model(Project project) {
        this.project = project;
    }

    public List<IDIYComponent> getComponents() {
        return project.getComponents();
    }

    public Size getGridSpacing() {
        return project.getGridSpacing();
    }
    
    public Model clone() {
        return new Model(project.clone());
    }
}
