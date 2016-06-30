package org.diylc.core

import java.nio.file.Path

import org.diylc.core.components.registry.ComponentRegistry
import org.diylc.core.serialization.ComponentModelModule
import org.diylc.core.serialization.JsonWriter

class ProjectSerializer {

    private JsonWriter writer = new JsonWriter()

    public ProjectSerializer(ComponentRegistry componentRegistry) {
        writer.mapper.registerModule(new ComponentModelModule(componentRegistry))
    }

    public void writeProject(Path path, Project project) {
        path.toFile().withOutputStream { OutputStream  outputStream ->
            writer.writeProject(outputStream, project)
        }
    }
}
