package org.diylc.components;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.diylc.common.ComponentType;
import org.diylc.components.boards.PerfBoard;
import org.diylc.core.IDIYComponent;
import org.junit.Test;

public class ComponentTypeLoaderTests {

    @Test
    public void test() {
        assertTrue(IDIYComponent.class.isAssignableFrom(PerfBoard.class));        
    }
    
    @Test
    public void testComponents() {
        Map<String, List<ComponentType>> findComponentTypes = new ComponentTypeLoader().loadComponentTypes();
        
        assertTrue(findComponentTypes.size() > 0);
    }

}
