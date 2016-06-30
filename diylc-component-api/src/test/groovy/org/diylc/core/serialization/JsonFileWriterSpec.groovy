package org.diylc.core.serialization

import org.diylc.core.Project
import org.diylc.core.ProjectFileManager

import spock.lang.Specification

import com.fasterxml.jackson.databind.JsonNode


class JsonFileWriterSpec extends Specification{

    void "read project 1"() {
        given:
            File inputFile = new File("./src/test/resources/7V fan regulator 2.diy")
            Project project = new ProjectFileManager().deserializeProjectFromFile(inputFile.toPath(), [])
            File outputFile = new File("./output1.diylc4")

        when:
            new JsonWriter().writeProject(outputFile, project)
            project = new JsonReader().readProject(outputFile)
            
        then:
            project != null
    }
}
