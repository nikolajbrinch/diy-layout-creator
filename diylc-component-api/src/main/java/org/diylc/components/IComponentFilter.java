package org.diylc.components;

import org.diylc.core.IDIYComponent;

public interface IComponentFilter {

	boolean testComponent(IDIYComponent<?> component);
}
