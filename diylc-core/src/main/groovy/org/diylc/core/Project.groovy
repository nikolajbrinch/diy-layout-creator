package org.diylc.core

import java.io.IOException
import java.io.Serializable
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set

import org.diylc.appframework.update.VersionNumber
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.annotations.PositiveMeasureValidator
import org.diylc.core.components.ComponentModel
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

import com.fasterxml.jackson.databind.ObjectMapper

import groovy.transform.EqualsAndHashCode;

/**
 * Entity class that defines a project. Contains project properties and a
 * collection of components.This class is serialized to file. Some filed getters
 * are tagged with {@link EditableProperty} to enable for user to edit them.
 * 
 * @author Branislav Stojkovic
 */
@EqualsAndHashCode
public class Project implements Serializable {

    private static final long serialVersionUID = 1L

    public static String DEFAULT_TITLE = "New Project"

    public static Size DEFAULT_WIDTH = new Size(29d, SizeUnit.cm)

    public static Size DEFAULT_HEIGHT = new Size(21d, SizeUnit.cm)

    public static Size DEFAULT_GRID_SPACING = new Size(0.1d, SizeUnit.in)

    VersionNumber fileVersion

    @EditableProperty(defaultable = false)
    String title

    @EditableProperty
    String author

    @EditableProperty(defaultable = false)
    String description

    @EditableProperty
    Size width

    @EditableProperty
    Size height

    @EditableProperty(name = "Grid spacing", validatorClass = SpacingValidator.class)
    Size gridSpacing

    private Map<String, IDIYComponent> componentLookup = new HashMap<>()

    List<IDIYComponent> components = new ArrayList<>()

    Set<Group> groups = new HashSet<>()

    Set<Double> lockedLayers = new HashSet<>()

    public Project() {
        this.title = DEFAULT_TITLE
        this.author = System.getProperty("user.name")
        this.width = DEFAULT_WIDTH
        this.height = DEFAULT_HEIGHT
        this.gridSpacing = DEFAULT_GRID_SPACING
    }

    @Override
    public int hashCode() {
        final int prime = 31
        int result = 1
        result = prime * result + ((author == null) ? 0 : author.hashCode())
        result = prime * result + ((components == null) ? 0 : components.hashCode())
        result = prime * result + ((description == null) ? 0 : description.hashCode())
        result = prime * result + ((height == null) ? 0 : height.hashCode())
        result = prime * result + ((title == null) ? 0 : title.hashCode())
        result = prime * result + ((width == null) ? 0 : width.hashCode())
        return result
    }

    @Override
    public boolean equals(Object obj) {
        if ((this.is(obj))) {
            return true
        }

        if (obj == null) {
            return false
        }

        if (getClass() != obj.getClass()) {
            return false
        }

        Project other = (Project) obj

        if (author == null) {
            if (other.author != null) {
                return false
            }
        } else if (!author.equals(other.author)) {
            return false
        }

        if (components == null) {
            if (other.components != null) {
                return false
            }
        } else if (components.size() != other.components.size()) {
            return false
        } else {
            Iterator<IDIYComponent> i1 = components.iterator()
            Iterator<IDIYComponent> i2 = other.components.iterator()

            while (i1.hasNext()) {
                IDIYComponent c1 = i1.next()
                IDIYComponent c2 = i2.next()

                if (!c1.equalsTo(c2)) {
                    return false
                }
            }
        }

        if (description == null) {
            if (other.description != null) {
                return false
            }
        } else if (!description.equals(other.description)) {
            return false
        }

        if (height == null) {
            if (other.height != null){
                return false
            }
        } else if (!height.equals(other.height)) {
            return false
        }

        if (title == null) {
            if (other.title != null) {
                return false
            }
        } else if (!title.equals(other.title)) {
            return false
        }

        if (width == null) {
            if (other.width != null) {
                return false
            }
        } else if (!width.equals(other.width)) {
            return false
        }

        return true
    }

    @Override
    public String toString() {
        return title
    }

    public static class SpacingValidator extends PositiveMeasureValidator {

        @Override
        public void validate(Object value) throws ValidationException {
            super.validate(value)
            Size size = (Size) value
            if (size.compareTo(new Size(0.1d, SizeUnit.mm)) < 0) {
                throw new ValidationException("must be at least 0.1mm")
            }
            if (size.compareTo(new Size(1d, SizeUnit.in)) > 0) {
                throw new ValidationException("must be less than 1in")
            }
        }
    }

    @Override
    public Project clone() {
        Project project = new Project()
        project.setTitle(this.getTitle())
        project.setAuthor(this.getAuthor())
        project.setDescription(this.getDescription())
        project.setFileVersion(this.getFileVersion())
        project.setGridSpacing(this.getGridSpacing())
        project.setHeight(this.getHeight())
        project.setWidth(this.getWidth())
        project.getLockedLayers().addAll(this.getLockedLayers())

        Map<IDIYComponent, IDIYComponent> cloneMap = new HashMap<IDIYComponent, IDIYComponent>()

        for (IDIYComponent component : this.components) {
            try {
                IDIYComponent clone = component.clone()
                project.getComponents().add(clone)
                cloneMap.put(component, clone)
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e)
            }
        }

        for (Group group : this.groups) {
            Group cloneGroup = new Group()

            for (String id : group.getComponents()) {
                cloneGroup.addComponent(id)
            }

            project.groups.add(cloneGroup)
        }
        return project
    }

    /**
     * Removes all the groups that contain at least one of the specified
     * components.
     *
     * @param components
     */
    public void ungroupComponents(List<IDIYComponent> components) {
        Iterator<Group> groupIterator = groups.iterator()

        while (groupIterator.hasNext()) {
            Group group = groupIterator.next()

            for (IDIYComponent component : components) {
                group.removeComponent(component.getId())
            }

            if (group.getComponents().isEmpty()) {
                groupIterator.remove()
            }
        }
    }

    /**
     * Create a new component group.
     *
     * @param components
     */
    public void groupComponents(List<IDIYComponent> components) {
        Group group = new Group()

        for (IDIYComponent component : components) {
            group.addComponent(component.getId())
        }

        groups.add(group)
    }

    /**
     * Finds all components that are grouped with the specified component. This
     * should be called any time components are added or removed from the
     * selection.
     *
     * @param component
     * @return set of all components that belong to the same group with the
     *         specified component. At the minimum, set contains that single
     *         component.
     */
    public Set<IDIYComponent> findAllGroupedComponents(IDIYComponent component) {
        Set<IDIYComponent> components = new HashSet<IDIYComponent>()

        components.add(component)

        for (Group group : groups) {
            if (group.containsComponent(component.getId())) {
                for (String id : group.getComponents()) {
                    components.add(componentLookup.get(id))
                }
                break
            }
        }

        return components
    }

    public void addComponentAt(int index, IDIYComponent component) {
        components.add(index, component)
        componentLookup.put(component.getId(), component)
    }

    public void addComponent(IDIYComponent component) {
        components.add(component)
        componentLookup.put(component.getId(), component)
    }

    public void removeComponents(List<IDIYComponent> components) {
        for (IDIYComponent component : components) {
            this.components.remove(component)
            componentLookup.remove(component.getId())
        }
    }

    public void bringComponentsToFront(List<IDIYComponent> components) {
        for (IDIYComponent component : components) {
            ComponentModel componentModel = component.getComponentModel()
            int index = this.components.indexOf(component)

            if (index >= 0) {
                // LOG.warn("Component not found in the project: " +
                // component.getName());
            } else {
                while (index < this.components.size() - 1) {
                    IDIYComponent componentAfter = this.components.get(index + 1)
                    ComponentModel componentAfterType = componentAfter.getComponentModel()
                    if (!componentModel.isFlexibleZOrder() && componentAfterType.getZOrder() > componentModel.getZOrder()) {
                        break
                    }

                    Collections.swap(this.components, index, index + 1)
                    index++
                }
            }
        }
    }

    public void sendComponentsToBack(List<IDIYComponent> components) {
        for (IDIYComponent component : components) {
            ComponentModel componentModel = component.getComponentModel()
            int index = this.components.indexOf(component)
            if (index < 0) {
                // LOG.warn("Component not found in the project: " +
                // component.getName());
            } else {
                while (index > 0) {
                    IDIYComponent componentBefore = this.components.get(index - 1)
                    ComponentModel componentBeforeType = componentBefore.getComponentModel()
                    if (!componentModel.isFlexibleZOrder() && componentBeforeType.getZOrder() < componentModel.getZOrder())
                        break
                    Collections.swap(this.components, index, index - 1)
                    index--
                }
            }
        }
    }

    public void createComponentLookup() {
        if (componentLookup == null) {
            componentLookup = new HashMap<>()
        }
        for (IDIYComponent component : components) {
            componentLookup.put(component.getId(), component)
        }
    }
}
