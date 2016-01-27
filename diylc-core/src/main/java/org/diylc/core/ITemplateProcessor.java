package org.diylc.core;

import java.util.List;

public interface ITemplateProcessor {

	void saveSelectedComponentAsTemplate(String templateName);

	List<Template> getTemplatesFor(String categoryName, String componentTypeName);
	
	List<Template> getTemplatesForSelection();
	
	void applyTemplateToSelection(Template template);
	
	void deleteTemplate(String categoryName, String componentTypeName, String templateName);

	public class TemplateAlreadyExistsException extends Exception {

		private static final long serialVersionUID = 1L;

	}

}
