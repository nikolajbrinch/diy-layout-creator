package org.diylc.app.menus.tools;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.diylc.app.view.bom.IActionProcessor;
import org.diylc.app.view.bom.ObjectListTable;

public class ObjectListTableTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		List<Object> projects = new ArrayList<Object>();
		projects.add(new Object());
		projects.add(new Object());

		ObjectListTable<Object> t = new ObjectListTable<Object>(Object.class,
				new String[] { "getName", "getDescription", "getCategory", "getOwner",
						"action:Download" }, new IActionProcessor<Object>() {

					@Override
					public void actionExecuted(Object value, String actionColumnName) {
						System.out.println("row clicked: " + actionColumnName + " - " + value);
					}

					@Override
					public Icon getActionIcon(String actionColumnName) {
						return null;
					}

					@Override
					public String getActionLabel(String actionColumnName) {
						return "get me";
					}
				});
		t.setData(projects);
		JFrame f = new JFrame();
		f.add(new JScrollPane(t));
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

}
