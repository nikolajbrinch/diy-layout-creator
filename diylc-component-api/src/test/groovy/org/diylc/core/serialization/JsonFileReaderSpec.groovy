package org.diylc.core.serialization

import org.diylc.core.IDIYComponent;
import org.diylc.core.Project
import org.diylc.core.ProjectFileManager
import org.diylc.core.components.registry.ComponentLookup
import org.diylc.core.components.registry.ComponentRegistry
import org.diylc.core.components.registry.DefaultComponentFactory
import org.diylc.core.serialization.JsonReader;
import org.diylc.core.serialization.JsonWriter

import spock.lang.Specification
import spock.lang.Unroll;

class JsonFileReaderSpec extends Specification {

    @Unroll("#inputName")
    void "read project"() {
        given:
        ComponentRegistry componentRegistry = new TestComponentRegistry(new ComponentLookup())
        File inputFile = new File("./src/test/resources/v3files/${inputName}")
        Project project = new ProjectFileManager(componentRegistry).deserializeProjectFromFile(inputFile.toPath(), [])
        File outputFile = new File("./${outputName}")

        when:
        new JsonWriter().writeProject(outputFile, project)
        project = new JsonReader(componentRegistry, new DefaultComponentFactory()).readProject(outputFile)

        then:
        project != null
        
        project.getComponents().each { IDIYComponent component ->
            assert component.id != null 
            assert !component.id.empty
        }

        where:
        inputName                              | outputName
        '7V fan regulator 2.xml'               | '7V fan regulator 2.diylc'
        '7V fan regulator.diy'                 | '7V fan regulator.diylc'
        'Bike light LED driver.diy'            | 'Bike light LED driver.diylc'
        'Bike lights dual.diy'                 | 'Bike lights dual.diylc'
        'Breadboard supply.diy'                | 'Breadboard supply.diylc'
        'OnOff-Relay.diy'                      | 'OnOff-Relay.diylc'
        'PWM - First try.diy'                  | 'PWM - First try.diylc'
        'PWM Arduino - wire.diy'               | 'PWM Arduino - wire.diylc'
        'PWM Arduino dual board - compact.diy' | 'PWM Arduino dual board - compact.diylc'
        'PWM Arduino dual board.diy'           | 'PWM Arduino dual board.diylc'
        'PWM Arduino small.diy'                | 'PWM Arduino small.diylc'
        'PWM Arduino.diy'                      | 'PWM Arduino.diylc'
        'PWM Dual output - Kickstart.diy'      | 'PWM Dual output - Kickstart.diylc'
        'PWM Dual output NE556.diy'            | 'PWM Dual output NE556.diylc'
        'PWM Kickstart.diy'                    | 'PWM Kickstart.diylc'
        'PWM Second go.diy'                    | 'PWM Second go.diylc'
    }
}
