package org.diylc.app.model;

import java.awt.Point;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.diylc.app.view.View;
import org.diylc.appframework.update.VersionNumber;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;

public interface Model {

    public boolean allowFileAction();

    public void dispose();

    public void loadProject(Project project, boolean freshStart) throws Exception;

    public void loadProject(Path path) throws Exception;

    public void saveProjectToFile(Path path, boolean isBackup);

    public Path getCurrentFile();

    public boolean isProjectModified();

    public Project getProject();

    public View getView();

    public void pasteComponents(List<IDIYComponent> components);

    public void setProject(Project project);

    /**
     * Finds all components at the specified location, sorted by z-index from
     * top to bottom.
     * 
     * @param point
     * @return
     */
    List<IDIYComponent> findComponentsAt(Point point);

    public boolean isComponentLocked(IDIYComponent component);

    public Set<IDIYComponent> getLockedComponents();

    public boolean isSnapToGrid();

    public Point getCenterOf(List<IDIYComponent> components, boolean snapToGrid);

    public VersionNumber getCurrentVersionNumber();

}
