package org.diylc.app;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import org.diylc.app.actions.CheckBoxAction;
import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.view.menus.MenuConstants;
import org.diylc.core.LRU;
import org.diylc.core.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DrawingManager {

    @Autowired
    Application application;
    
    private final Map<String, Drawing> drawings = new LinkedHashMap<>();

    private Drawing currentDrawing;

    public void setCurrentDrawing(String drawingId) {
        if (drawingId != null) {
            Drawing drawing = getDrawing(drawingId);
            drawing.getView().requestFocus();

            currentDrawing = drawing;
        }
    }

    public Drawing getCurrentDrawing() {
        return currentDrawing;
    }

    public void refreshActions() {
        getDrawings().stream().forEach((drawing) -> drawing.getView().refreshActions());
    }

    public Drawing newDrawing(ApplicationController applicationController, Project project, Path path, boolean load) {
        Drawing drawing = new Drawing(applicationController, project, path, load);

        drawings.put(drawing.getId(), drawing);

        setCurrentDrawing(drawing.getId());

        return drawing;
    }

    public List<Drawing> getDrawings() {
        return new ArrayList<Drawing>(drawings.values());
    }

    public Drawing getDrawing(String drawingId) {
        return drawings.get(drawingId);
    }

    public void removeDrawing(String drawingId) {
        drawings.remove(drawingId);
    }

    public boolean isEmpty() {
        return drawings.isEmpty();
    }

    public Drawing getFirstDrawing() {
        return getDrawings().get(0);
    }

    public void updateLru(LRU<Path> lru) {
        getDrawings().stream().forEach((drawing) -> drawing.getView().updateLru(lru));
    }

    public boolean closeAll() {
        return getDrawings().stream().map((drawing) -> drawing.close()).noneMatch((closed) -> !closed);
    }

    public void closeDrawing(String drawingId) {
        Drawing drawing = getDrawing(drawingId);

        if (drawing != null && drawing.close()) {
            removeDrawing(drawingId);
            setCurrentDrawing(isEmpty() ? null : getFirstDrawing().getId());

            refreshActions();
        }
    }

    public void updateWindowMenus(Drawing activeDrawing) {
        for (Drawing drawing : getDrawings()) {
            boolean checked = drawing.getId().equals(activeDrawing);
            Action action = new CheckBoxAction(drawing.getTitle(), checked, (event) -> application.switchWindow(drawing.getId()));
            action.putValue("UUID", drawing.getId());
            drawing.getView().addMenuAction(action, MenuConstants.WINDOW_MENU);
        }
    }

}
