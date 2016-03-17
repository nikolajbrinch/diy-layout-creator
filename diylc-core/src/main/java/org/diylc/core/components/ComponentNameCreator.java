package org.diylc.core.components;

import org.diylc.core.Project;

import java.util.Arrays;
import java.util.List;

public class ComponentNameCreator {

    public static String createComponentName(ComponentModel componentModel, Project project) {

        boolean exists = true;

        List<IDIYComponent> components = project.getComponents();

        String[] takenNames = new String[components.size()];

        for (int j = 0; j < project.getComponents().size(); j++) {
            takenNames[j] = components.get(j).getName();
        }

        Arrays.sort(takenNames);

        String name = null;

        int i = 1;
        while (exists) {
            name = componentModel.getNamePrefix() + i;
            exists = (Arrays.binarySearch(takenNames, name) >= 0);
            i++;
        }

        return name;
    }

}
