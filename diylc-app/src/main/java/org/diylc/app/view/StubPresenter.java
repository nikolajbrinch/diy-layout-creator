package org.diylc.app.view;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import org.diylc.app.Drawing;
import org.diylc.app.DrawingManager;
import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.model.Model;
import org.diylc.app.view.canvas.Canvas;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.components.registry.ComponentFactory;
import org.diylc.core.components.registry.ComponentRegistry;
import org.diylc.core.platform.QuitResponse;

public class StubPresenter extends Presenter {

    public StubPresenter() {
        super(new View() {

            @Override
            public int showConfirmDialog(String message, String title, int optionType, int messageType) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public boolean editProperties(List<PropertyWrapper> properties, Set<PropertyWrapper> defaultedProperties) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Path promptFileSave() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void refreshActions() {
                // TODO Auto-generated method stub

            }

            @Override
            public Canvas getCanvas() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void addPluginComponent(JComponent component, int index) throws BadPositionException {
                // TODO Auto-generated method stub

            }

            @Override
            public double getZoomLevel() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public void setZoomLevel(double zoomLevel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void block() {
                // TODO Auto-generated method stub

            }

            @Override
            public void unblock() {
                // TODO Auto-generated method stub

            }

            @Override
            public void showMessage(String message, String title, int messageType) {
                // TODO Auto-generated method stub

            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public JFrame getFrame() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public DrawingController getController() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public JMenuItem addMenuAction(Action action, String menuName) {
                return null;
            }

            @Override
            public void addSubmenu(String name, Icon icon, String parentMenuName) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removeMenuAction(Action action, String menuName) {
                // TODO Auto-generated method stub

            }

            @Override
            public void clearMenuItems(String menuName) {
                // TODO Auto-generated method stub

            }

            @Override
            public Drawing getDrawing() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void repaintCanvas() {
                // TODO Auto-generated method stub

            }

            @Override
            public void updateStatusBar() {
                // TODO Auto-generated method stub

            }

            @Override
            public void updateStatusBar(String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void updateZoomLevel(double zoomLevel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void updateLockedLayers() {
                // TODO Auto-generated method stub

            }

            @Override
            public void updateTitle() {
                // TODO Auto-generated method stub

            }

            @Override
            public void initCanvas(Project project, boolean freshStart) {
                // TODO Auto-generated method stub

            }

            @Override
            public void selectionStateChanged(List<IDIYComponent> selection, Collection<IDIYComponent> stuckComponents) {
                // TODO Auto-generated method stub

            }

            @Override
            public void minimize() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void zoom() {
                // TODO Auto-generated method stub
                
            }

        }, new DrawingController(new ApplicationController() {

            @Override
            public void removeLruPath(Path path) {
                // TODO Auto-generated method stub

            }

            @Override
            public void open(Path path) {
                // TODO Auto-generated method stub

            }

            @Override
            public void open() {
                // TODO Auto-generated method stub

            }

            @Override
            public void importProject() {
                // TODO Auto-generated method stub

            }

            @Override
            public Drawing getCurrentDrawing() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void exit(QuitResponse response) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addLruPath(Path path) {
                // TODO Auto-generated method stub

            }

            @Override
            public void autoSave(Project project) {
                // TODO Auto-generated method stub

            }

            @Override
            public void switchWindow(String drawingId) {
                // TODO Auto-generated method stub

            }

            @Override
            public void closeDrawing(String drawingId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public Drawing createProject(Project project, Path path) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ComponentRegistry getComponentRegistry() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ComponentFactory getComponentFactory() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public DrawingManager getDrawingManager() {
                // TODO Auto-generated method stub
                return null;
            }
        }, new Model() {

            @Override
            public void loadProject(Project project, boolean freshStart) {
                // TODO Auto-generated method stub

            }

            @Override
            public List<IDIYComponent> getSelectedComponents() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }
        }), Paths.get(""), false);
    }

}
