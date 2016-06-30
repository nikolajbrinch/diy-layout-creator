package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.Project
import org.diylc.core.components.registry.ComponentRegistry

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

@CompileStatic
class JsonWriter {

    ObjectMapper mapper = new ObjectMapper()

    public JsonWriter() {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        mapper.registerModule(new SizeModule())
        mapper.registerModule(new VoltageModule())
        mapper.registerModule(new PowerModule())
        mapper.registerModule(new CapacitanceModule())
        mapper.registerModule(new ResistanceModule())
        mapper.registerModule(new InductanceModule())
        mapper.registerModule(new CurrentModule())
        mapper.registerModule(new ColorModule())
        mapper.registerModule(new FontModule())
        mapper.addMixIn(Project.class, ComponentLookupMixIn.class)
    }

    public void writeProject(File file, Project project) {
        file.withOutputStream { outputStream ->
            writeProject(outputStream, project)
        }
    }

    public void writeProject(OutputStream outputStream, Project project) {
        mapper.writeValue(outputStream, mapper.convertValue(project, JsonNode.class))
    }

}
