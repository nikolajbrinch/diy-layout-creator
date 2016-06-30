
package org.diylc.core

import groovy.transform.CompileStatic

import java.nio.file.Path

import org.diylc.core.components.registry.ComponentFactory
import org.diylc.core.components.registry.ComponentRegistry
import org.diylc.core.serialization.ComponentModelModule
import org.diylc.core.serialization.ComponentModule
import org.diylc.core.serialization.JsonReader

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.format.DataFormatDetector
import com.fasterxml.jackson.core.format.DataFormatMatcher
import com.fasterxml.jackson.core.format.MatchStrength
import com.fasterxml.jackson.dataformat.xml.XmlFactory


@CompileStatic
class ProjectDeserializer {

    private final JsonFactory jsonFactory = new JsonFactory()

    private final XmlFactory xmlFactory = new XmlFactory()

    private final DataFormatDetector detector

    private final ProjectFileManager projectFileManager
    
    private final JsonReader reader = new JsonReader()

    public ProjectDeserializer(ComponentRegistry componentRegistry, ComponentFactory componentFactory) {
        detector = new DataFormatDetector([jsonFactory, xmlFactory]).withMinimalMatch(MatchStrength.WEAK_MATCH).withOptimalMatch(MatchStrength.SOLID_MATCH)
        reader.mapper.registerModule(new ComponentModule(componentRegistry, componentFactory))
        reader.mapper.registerModule(new ComponentModelModule(componentRegistry))
        projectFileManager = new ProjectFileManager(componentRegistry)
    }

    public Project readProject(Path path) {
        Project project

        path.toFile().withInputStream { InputStream inputStream ->
            inputStream.mark(0)
            DataFormatMatcher match = detector.findFormat(inputStream)
            
            inputStream.reset()
            
            if (xmlFactory.getFormatName().equals(match.getMatchedFormatName())) {
                project = projectFileManager.deserializeProjectFromFile(path, [])
            } else if (jsonFactory.getFormatName().equals(match.getMatchedFormatName())) {
                project = reader.readProject(inputStream)
            } else {
                throw new IllegalStateException("Unknown file format!")
            }
        }
        
        return project
    }
}
