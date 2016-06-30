package org.diylc.app.controllers;

import java.nio.file.Path;
import java.util.Collection;

import org.diylc.app.Drawing;
import org.diylc.components.registry.ComponentRegistry;
import org.diylc.core.Project;
import org.diylc.core.platform.QuitResponse;

public interface ApplicationController {

    public Drawing getCurrentDrawing();

    public void setCurrentDrawing(Drawing drawing);

    /**
     * Create new project action
     * @return
     */
    public Drawing createProject(Project project, Path path);

    /**
     * Open action
     */
    public void open();

    /**
     * Open recent action
     * 
     * @param path
     */
    public void open(Path path);

    /**
     * Import action
     */
    public void importProject();

    /**
     * Exit action
     * 
     * @param response
     */
    public void exit(QuitResponse response);

    /**
     * Add recent file to LRU list
     * 
     * @param file
     */
    public void addLruPath(Path path);

    /**
     * Remove recent file from LRU list
     * 
     * @param file
     */
    public void removeLruPath(Path path);

    public void autoSave(Project project);

    public Collection<Drawing> getDrawings();

    public void switchWindow(String drawingId);

    public void closeDrawing(String drawingId);

    public ComponentRegistry getComponentRegistry();

}
