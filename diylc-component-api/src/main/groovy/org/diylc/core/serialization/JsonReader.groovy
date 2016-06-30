package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.Project

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper


@CompileStatic
class JsonReader {

    ObjectMapper mapper = new ObjectMapper()

    JsonReader() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.registerModule(new SizeModule())
        mapper.registerModule(new ColorModule())
        mapper.registerModule(new FontModule())
        mapper.registerModule(new VoltageModule())
        mapper.registerModule(new CurrentModule())
        mapper.registerModule(new PowerModule())
        mapper.registerModule(new ResistanceModule())
        mapper.registerModule(new CapacitanceModule())
        mapper.registerModule(new InductanceModule())
    }

    public Project readProject(File file) {
        Project project

        file.withInputStream { inputStream ->
            project = readProject(inputStream)
        }

        return project
    }

    public Project readProject(InputStream inputStream) {
        JsonNode projectNode = mapper.readTree(inputStream)

        return createProject(projectNode)
    }

    private Project createProject(JsonNode jsonNode) {
        Project project = mapper.convertValue(jsonNode, Project.class)

        project.createComponentLookup()

        return project
    }
}
