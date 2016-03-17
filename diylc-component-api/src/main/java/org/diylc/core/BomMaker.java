package org.diylc.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.diylc.core.components.BomPolicy;
import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.IDIYComponent;
import org.diylc.core.components.registry.ComponentRegistry;

public class BomMaker {

	private static BomMaker instance = new BomMaker();

	public static BomMaker getInstance() {
		return instance;
	}

	private BomMaker() {
	}

	public List<BomEntry> createBom(ComponentRegistry componentRegistry, List<IDIYComponent> components) {
		Map<String, BomEntry> entryMap = new LinkedHashMap<String, BomEntry>();
		List<IDIYComponent> sortedComponents = new ArrayList<IDIYComponent>(
				components);
		Collections.sort(sortedComponents, new Comparator<IDIYComponent>() {

			@Override
			public int compare(IDIYComponent o1, IDIYComponent o2) {
				String name1 = o1.getName();
				String name2 = o2.getName();
				Pattern p = Pattern.compile("(\\D+)(\\d+)");
				Matcher m1 = p.matcher(name1);
				Matcher m2 = p.matcher(name2);
				if (m1.matches() && m2.matches()) {
					String prefix1 = m1.group(1);
					int value1 = Integer.parseInt(m1.group(2));
					String prefix2 = m2.group(1);
					int value2 = Integer.parseInt(m2.group(2));
					int compare = prefix1.compareToIgnoreCase(prefix2);
					if (compare != 0) {
						return compare;
					}
					return new Integer(value1).compareTo(value2);
				}				
				return name1.compareToIgnoreCase(name2);
			}			
		});
		for (IDIYComponent component : sortedComponents) {
			ComponentModel model = component.getComponentModel();
			if (model.getBomPolicy() == BomPolicy.NEVER_SHOW)
				continue;
			String name = component.getName();
			String value;
			try {
				value = component.getValueForDisplay();
			} catch (Exception e){
				value = "<undefined>";
			}
			if ((name != null) && (value != null)) {
				String key = model.getName() + "|" + value;
				if (entryMap.containsKey(key)) {
					BomEntry entry = entryMap.get(key);
					entry.setQuantity(entry.getQuantity() + 1);
					if (model.getBomPolicy() == BomPolicy.SHOW_ALL_NAMES) {
						entry.setName(entry.getName() + ", " + name);
					}
				} else {
					entryMap.put(key, new BomEntry(model.getName(), model
							.getBomPolicy() == BomPolicy.SHOW_ALL_NAMES ? name
							: model.getName(), value, 1));
				}
			}
		}
		List<BomEntry> bom = new ArrayList<BomEntry>(entryMap.values());
		return bom;
	}
}
