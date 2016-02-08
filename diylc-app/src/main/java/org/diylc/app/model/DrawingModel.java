package org.diylc.app.model;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.diylc.app.io.ProjectFileManager;
import org.diylc.app.utils.CalcUtils;
import org.diylc.app.utils.ModelUtils;
import org.diylc.app.view.DrawingView;
import org.diylc.app.view.Presenter;
import org.diylc.app.view.View;
import org.diylc.app.view.rendering.DrawingManager;
import org.diylc.appframework.update.VersionNumber;
import org.diylc.components.registry.ComponentRegistry;
import org.diylc.components.registry.ComponentType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawingModel implements Model {

    public static VersionNumber CURRENT_VERSION = new VersionNumber(4, 0, 0);

    private static final Logger LOG = LoggerFactory.getLogger(DrawingModel.class);

    private ProjectFileManager projectFileManager;

    private Project project;

    private DrawingView view;

    private Presenter presenter;

    public DrawingModel() {
        Project project = new Project();
        try {
            ModelUtils.fillWithDefaultProperties(project, null);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | SecurityException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        this.project = new Project();
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

    @Override
    public void saveProjectToFile(Path path, boolean isBackup) {
        LOG.trace(String.format("saveProjectToFile(%s)", path.toAbsolutePath()));
        try {
            getProject().setFileVersion(CURRENT_VERSION);
            projectFileManager.serializeProjectToFile(getProject(), path, isBackup);
            if (!isBackup) {
                Configuration.INSTANCE.setLastPath(path.getParent());
            }
        } catch (Exception ex) {
            LOG.error("Could not save file", ex);
            if (!isBackup) {
                getView().showMessage("Could not save file " + path.toAbsolutePath() + ". Check the log for details.", "Error",
                        View.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public Path getCurrentFile() {
        return projectFileManager.getCurrentFile();
    }

    @Override
    public boolean isProjectModified() {
        return projectFileManager.isModified();
    }

    @Override
    public DrawingView getView() {
        return view;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    public void setView(DrawingView view) {
        this.view = view;
        this.presenter = view.getPresenter();
    }

    @Override
    public List<IDIYComponent> findComponentsAt(Point point) {
        return findComponentsAtScaled(getView().scalePoint(point));
    }

    /**
     * Finds all components whose areas include the specified {@link Point}.
     * Point is <b>not</b> scaled by the zoom factor. Components that belong to
     * locked layers are ignored.
     *
     * @return
     */
    private List<IDIYComponent> findComponentsAtScaled(Point point) {
        List<IDIYComponent> components = getDrawingManager().findComponentsAt(point, getProject());
        Iterator<IDIYComponent> iterator = components.iterator();

        while (iterator.hasNext()) {
            if (isComponentLocked(iterator.next())) {
                iterator.remove();
            }
        }

        return components;
    }

    @Override
    public Set<IDIYComponent> getLockedComponents() {
        Set<IDIYComponent> lockedComponents = new HashSet<IDIYComponent>();

        for (IDIYComponent component : getProject().getComponents()) {
            if (isComponentLocked(component)) {
                lockedComponents.add(component);
            }
        }

        return lockedComponents;
    }

    @Override
    public boolean isComponentLocked(IDIYComponent component) {
        ComponentType componentType = ComponentRegistry.INSTANCE.getComponentType(component);

        return getProject().getLockedLayers().contains((double) Math.round(componentType.getZOrder()));
    }

    @Override
    public boolean isSnapToGrid() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Point getCenterOf(List<IDIYComponent> components, boolean snapToGrid) {
        // Determine center of rotation
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (IDIYComponent component : components) {
            for (int i = 0; i < component.getControlPointCount(); i++) {
                Point p = component.getControlPoint(i);
                if (minX > p.x) {
                    minX = p.x;
                }
                if (maxX < p.x) {
                    maxX = p.x;
                }
                if (minY > p.y) {
                    minY = p.y;
                }
                if (maxY < p.y) {
                    maxY = p.y;
                }
            }
        }
        int centerX = (maxX + minX) / 2;
        int centerY = (maxY + minY) / 2;

        if (snapToGrid) {
            CalcUtils.roundToGrid(centerX, getProject().getGridSpacing());
            CalcUtils.roundToGrid(centerY, getProject().getGridSpacing());
        }

        return new Point(centerX, centerY);
    }

    /**
     * XXX: Must go away!
     */
    @Deprecated
    DrawingManager getDrawingManager() {
        return getView().getDrawingManager();
    }

    @Override
    public VersionNumber getCurrentVersionNumber() {
        return CURRENT_VERSION;
    }
}
