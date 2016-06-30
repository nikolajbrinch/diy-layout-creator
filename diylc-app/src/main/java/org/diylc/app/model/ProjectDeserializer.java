package org.diylc.app.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.diylc.app.Application;
import org.diylc.app.view.View;
import org.diylc.core.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectDeserializer {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectDeserializer.class);

    private static ProjectFileManager projectFileManager = new ProjectFileManager();

    public static Project loadProjectFromFile(Path path) throws Exception {
        LOG.trace(String.format("loadProjectFromFile(%s)", path.toAbsolutePath()));
        List<String> warnings = new ArrayList<String>();
        Project project = (Project) projectFileManager.deserializeProjectFromFile(path, warnings);

        if (!warnings.isEmpty()) {
            StringBuilder builder = new StringBuilder("<html>File was opened, but there were some issues with it:<br><br>");
            for (String warning : warnings) {
                builder.append(warning);
                builder.append("<br>");
            }
            builder.append("</html");

            Application.getApplication().showMessage(builder.toString(), "Warning", View.WARNING_MESSAGE);
        }

        return project;
    }

}
