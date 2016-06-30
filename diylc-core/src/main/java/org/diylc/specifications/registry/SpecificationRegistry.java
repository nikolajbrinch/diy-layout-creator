package org.diylc.specifications.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.diylc.specifications.Specification;

public class SpecificationRegistry {

    private Map<String, Map<String, Specification>> specifications = new HashMap<String, Map<String, Specification>>();
    
    public void add(Specification specification) {
        Map<String, Specification> specifications = this.specifications.get(specification.getCategory());

        if (specifications == null) {
            specifications = new HashMap<String, Specification>();
            this.specifications.put(specification.getCategory(), specifications);
        }

        specifications.put(specification.getId(), specification);
    }

    public Collection<Specification> get(String category) {
        Collection<Specification> specifications = null;
        
        if (category != null) {
            specifications = this.specifications.get(category).values();
        } 
        
        return specifications;
    }

}
