package org.diylc.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.diylc.components.AbstractComponent;
import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.registry.ComponentLookup;
import org.diylc.core.components.registry.ComponentRegistry;
import org.diylc.core.components.registry.DefaultComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ProjectFileManager {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectFileManager.class);

    private final XStream xStream;

    /*
     * Legacy deserializer for 3.0.1 through 3.0.7, loads Points referenced in
     * pixels.
     */
    private final XStream xStreamOld;

    private final ComponentRegistry componentRegistry;

    public ProjectFileManager(ComponentRegistry componentRegistry) {
        super();
        this.componentRegistry = componentRegistry;
        this.xStream = new XStream(new DomDriver("UTF-8"));
        xStream.autodetectAnnotations(true);
        xStream.registerConverter(new PointConverter());
        this.xStreamOld = new XStream(new DomDriver());
        xStreamOld.autodetectAnnotations(true);
    }

    public synchronized void serializeProjectToFile(Project project, Path path) throws IOException {
        LOG.info(String.format("saveProjectToFile(%s)", path.toAbsolutePath()));

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), "UTF-8")) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            xStream.toXML(project, writer);
        }
    }

    public Project deserializeProjectFromFile(Path path, List<String> warnings) throws SAXException, IOException,
            ParserConfigurationException {
        LOG.info(String.format("loadProjectFromFile(%s)", path));
        Project project = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new InputStreamReader(Files.newInputStream(path))));
        doc.getDocumentElement().normalize();

        if (doc.getDocumentElement().getNodeName().equalsIgnoreCase(Project.class.getName())) {
            project = parseV3File(path);
        }

        Collections.sort(warnings);

        project.createComponentLookup();

        return project;
    }

    private Project parseV3File(Path path) throws IOException {
        Project project = null;

        try (Reader reader = new InputStreamReader(Files.newInputStream(path), "UTF-8")) {
            project = (Project) xStream.fromXML(reader);
        } catch (Exception e) {
            LOG.warn("Could not open with the new xStream, trying the old one");
        }

        if (project == null) {
            try (InputStream inpuStream = Files.newInputStream(path)) {
                project = (Project) xStreamOld.fromXML(inpuStream);
            } catch (Exception e) {
                /*
                 * Ignore
                 */
            }
        }
        
        Map<String, String> nameToId = new HashMap<>(); 

        ComponentLookup componentLookup = new ComponentLookup();
        
        if (project != null) {
            List<IDIYComponent> components = project.getComponents();

            for (IDIYComponent component : components) {
                String id = component.getId();
                if (id == null || id.isEmpty()) {
                    id = DefaultComponentFactory.createUniqueId();
                    setId(component, id);
                }
                nameToId.put(component.getName(), id);
                ComponentModel componentModel = componentRegistry.getComponentModel(componentLookup.getComponentModelId(component.getClass().getName()));
                component.setComponentModel(componentModel);
//                if (component instanceof RadialFilmCapacitor) {
//                    Voltage voltage = ((RadialFilmCapacitor) component).getVoltageNew();
//                    if (voltage.getValue() == null || voltage.getUnit() == null) {
//                        org.diylc.components.passive.Voltage oldVoltage = ((RadialFilmCapacitor) component).getVoltage();
//                        if (oldVoltage != null) {
//                            ((RadialFilmCapacitor) component).setVoltageNew(oldVoltage.convertToNewFormat());
//                        }
//                    }
//                }
            }
        }
        
        convertGroups(project, nameToId);

        return project;
    }

    private void convertGroups(Project project, Map<String, String> nameToId) {
        Set<Group> newGroups = new HashSet<>();
        
        Collection groups = project.getGroups();
        
        for (Object group : groups) {
            Group newGroup = new Group();
            if (group instanceof Set) {
                Set<IDIYComponent> setGroup = (Set<IDIYComponent>) group;
                
                for (IDIYComponent component : setGroup) {
                    newGroup.addComponent(nameToId.get(component.getName()));
                }
            }
            newGroups.add(newGroup);
        }
        
        project.getGroups().clear();
        for (Group group : newGroups) {
            project.getGroups().add(group);
        }
    }

    private void setId(IDIYComponent component, String id) {
        ((AbstractComponent) component).setId(id);
    }
}