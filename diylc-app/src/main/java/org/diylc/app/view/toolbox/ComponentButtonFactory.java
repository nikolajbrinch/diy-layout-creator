package org.diylc.app.view.toolbox;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Template;
import org.diylc.core.components.ComponentModel;
import org.openide.awt.DropDownButtonFactory;

/**
 * {@link JButton} that displays component type icon and instantiates the
 * component when clicked.
 * 
 * @author Branislav Stojkovic
 */
class ComponentButtonFactory {

    public static int MARGIN = 3;

    public static JButton create(final IPlugInPort plugInPort, final ComponentModel componentModel, final JPopupMenu menu) {
        JButton button = DropDownButtonFactory.createDropDownButton(componentModel.getIcon(), menu);

        button.setBorder(BorderFactory.createEmptyBorder(MARGIN + 1, MARGIN + 1, MARGIN, MARGIN));

        button.setToolTipText("<html><b>" + componentModel.getName() + "</b><br>" + componentModel.getDescription() + "<br>Author: "
                + componentModel.getAuthor() + "<br><br>Right click to select all components of this type" + "</html>");

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                plugInPort.setNewComponentTypeSlot(componentModel, null);
            }
        });

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    List<IDIYComponent> components = plugInPort.getCurrentProject().getComponents();
                    List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();
                    for (IDIYComponent component : components) {
                        if (componentModel.getInstanceClass().equals(component.getClass())) {
                            newSelection.add(component);
                        }
                    }
                    // Ctrl appends selection
                    if (e.isControlDown()) {
                        newSelection.addAll(plugInPort.getSelectedComponents());
                    }
                    plugInPort.updateSelection(newSelection);
                    plugInPort.setNewComponentTypeSlot(null, null);
                    plugInPort.refresh();
                }
            }
        });

        button.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                plugInPort.keyPressed(e.getKeyCode(), e.isControlDown(), e.isShiftDown(), e.isAltDown(), e.isMetaDown());
            }
        });
        return button;
    }

    public static JMenuItem createTemplateItem(final IPlugInPort plugInPort, final Template template, final ComponentModel componentModel) {
        final JMenuItem item = new JMenuItem(template.getName()) {

            private static final long serialVersionUID = 1L;

            /*
             * Customize item size to fit the delete button(non-Javadoc)
             * 
             * @see javax.swing.JComponent#getPreferredSize()
             */
            public java.awt.Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 18, d.height);
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                plugInPort.setNewComponentTypeSlot(componentModel, template);
            }
        });
        JLabel label = new JLabel(AppIconLoader.Garbage.getIcon());
        label.setToolTipText("Delete template");
        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                /*
                 * Hide the menu
                 */
                Container c = item.getParent();
                if (c != null && c instanceof JPopupMenu) {
                    JPopupMenu m = (JPopupMenu) c;
                    m.setVisible(false);
                }
                int result = JOptionPane.showConfirmDialog(SwingUtilities.getRoot(item), "Are you sure you want to delete template \""
                        + template.getName() + "\"", "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
                plugInPort.deleteTemplate(componentModel.getCategory(), componentModel.getName(), template.getName());
                e.consume();
            }
        });
        item.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        item.add(label);
        return item;
    }
}
