package org.diylc.app.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.diylc.app.update.UpdateLabel;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.utils.StringUtils;
import org.diylc.components.registry.ComponentType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.config.Configuration;
import org.diylc.core.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusBar extends JPanel implements IPlugIn {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(StatusBar.class);

    public static String UPDATE_URL = "http://www.diy-fever.com/update.xml";

    private static final Format sizeFormat = new DecimalFormat("0.##");

    private JComboBox<Double> zoomBox;

    private UpdateLabel updateLabel;

    private MemoryBar memoryPanel;

    private JLabel statusLabel;

    private JLabel sizeLabel;

    private IPlugInPort plugInPort;

    // State variables
    private ComponentType componentSlot;

    private Point controlPointSlot;

    private List<String> componentNamesUnderCursor;

    private List<String> selectedComponentNames;

    private List<String> stuckComponentNames;

    private String statusMessage;

    public StatusBar(ISwingUI swingUI) {
        super();

        setLayout(new GridBagLayout());

        try {
            swingUI.injectGUIComponent(this, SwingUtilities.BOTTOM);
        } catch (BadPositionException e) {
            LOG.error("Could not install status bar", e);
        }
    }

    public JLabel getSizeLabel() {
        if (sizeLabel == null) {
            sizeLabel = new JLabel(AppIconLoader.Size.getIcon());
            sizeLabel.setFocusable(true);
            sizeLabel.setToolTipText("Click to calculate selection size");
            sizeLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    Point2D size = plugInPort.calculateSelectionDimension();
                    boolean metric = Configuration.INSTANCE.getMetric();
                    String text;
                    if (size == null) {
                        text = "Selection is empty.";
                    } else {
                        text = "Selection size: " + sizeFormat.format(size.getX()) + " x " + sizeFormat.format(size.getY())
                                + (metric ? " cm" : " in");
                    }
                    JOptionPane.showMessageDialog(SwingUtilities.getRootPane(StatusBar.this), text, "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        return sizeLabel;
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        this.plugInPort = plugInPort;

        layoutComponents();
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        add(getStatusLabel(), gbc);

        JPanel zoomPanel = new JPanel(new BorderLayout());
        zoomPanel.add(new JLabel("Zoom: "), BorderLayout.WEST);
        zoomPanel.add(getZoomBox(), BorderLayout.CENTER);
        zoomPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0;
        add(zoomPanel, gbc);

        gbc.gridx = 2;
        add(getSizeLabel(), gbc);

        gbc.gridx = 3;
        add(getUpdateLabel(), gbc);

        gbc.gridx = 4;
        gbc.fill = GridBagConstraints.NONE;
        add(getMemoryPanel(), gbc);

        gbc.gridx = 5;
        add(new JPanel(), gbc);
    }

    private JComboBox<Double> getZoomBox() {
        if (zoomBox == null) {
            zoomBox = new JComboBox<Double>(plugInPort.getAvailableZoomLevels());
            zoomBox.setSelectedItem(plugInPort.getZoomLevel());
            zoomBox.setFocusable(false);
            zoomBox.setRenderer(new PercentageListCellRenderer());
            zoomBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    plugInPort.setZoomLevel((Double) zoomBox.getSelectedItem());
                }
            });
        }
        return zoomBox;
    }

    private UpdateLabel getUpdateLabel() {
        if (updateLabel == null) {
            updateLabel = new UpdateLabel(plugInPort.getCurrentVersionNumber(), UPDATE_URL, AppIconLoader.LightBulbOn.getIcon(),
                    AppIconLoader.LightBulbOff.getIcon());
            updateLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        }
        return updateLabel;
    }

    private MemoryBar getMemoryPanel() {
        if (memoryPanel == null) {
            memoryPanel = new MemoryBar(false);
        }
        return memoryPanel;
    }

    private JLabel getStatusLabel() {
        if (statusLabel == null) {
            statusLabel = new JLabel();
            statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        }
        return statusLabel;
    }

    private void refreshStatusText() {
        String statusText = this.statusMessage;

        if (componentSlot == null) {
            if (componentNamesUnderCursor != null && !componentNamesUnderCursor.isEmpty()) {
                String formattedNames = StringUtils.toCommaString(componentNamesUnderCursor);
                statusText = "<html>Drag control point(s) of " + formattedNames + "</html>";
            } else if (selectedComponentNames != null && !selectedComponentNames.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append(StringUtils.toCommaString(selectedComponentNames.subList(0, Math.min(20, selectedComponentNames.size()))));
                if (selectedComponentNames.size() > 15) {
                    builder.append(" and " + (selectedComponentNames.size() - 15) + " more");
                }
                if (!stuckComponentNames.isEmpty()) {
                    String key = SystemUtils.isMac() ? "⌘ Cmd" : "Ctrl";
                    builder.append(" (hold <b>" + key + "</b> and drag to unstuck from ");
                    builder.append(StringUtils.toCommaString(stuckComponentNames.subList(0, Math.min(5, stuckComponentNames.size()))));
                    if (stuckComponentNames.size() > 5) {
                        builder.append(" and " + (stuckComponentNames.size() - 5) + " more");
                    }
                    builder.append(")");
                }
                statusText = "<html>Selection: " + builder.toString() + "</html>";
            }
        } else {
            switch (componentSlot.getCreationMethod()) {
            case POINT_BY_POINT:
                String count;
                if (controlPointSlot == null) {
                    count = "first";
                } else {
                    count = "second";
                }
                statusText = "<html>Click on the canvas to set the " + count + " control point of a new <font color='blue'>"
                        + componentSlot.getName() + "</font> or press <b>Esc</b> to cancel</html>";
                break;
            case SINGLE_CLICK:
                statusText = "<html>Click on the canvas to create a new <font color='blue'>" + componentSlot.getName()
                        + "</font> or press <b>Esc</b> to cancel</html>";
                break;
            }
        }
        final String finalStatus = statusText;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                getStatusLabel().setText(finalStatus);
            }
        });
    }

    public void update() {
        componentSlot = plugInPort.getNewComponentTypeSlot();
        controlPointSlot = plugInPort.getFirstControlPoint();
        componentNamesUnderCursor = new ArrayList<String>();
        for (IDIYComponent component : plugInPort.getAvailableControlPoints().keySet()) {
            componentNamesUnderCursor.add("<font color='blue'>" + component.getName() + "</font>");
        }
        Collections.sort(componentNamesUnderCursor);

        refreshStatusText();
    }

    public void update(String message) {
        statusMessage = message;

        refreshStatusText();
    }

    public void updateZoomLevel(double zoomLevel) {
        if (!(zoomLevel == (Double) getZoomBox().getSelectedItem())) {
            final Double zoom = (Double) zoomLevel;
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getZoomBox().setSelectedItem(zoom);
                }
            });
        }

    }

    public void updateSelectionState(List<IDIYComponent> selection, Collection<IDIYComponent> stuckComponents) {
        Collection<String> componentNames = new HashSet<String>();
        for (IDIYComponent component : selection) {
            componentNames.add("<font color='blue'>" + component.getName() + "</font>");
        }
        this.selectedComponentNames = new ArrayList<String>(componentNames);
        Collections.sort(this.selectedComponentNames);
        this.stuckComponentNames = new ArrayList<String>();
        for (IDIYComponent component : stuckComponents) {
            this.stuckComponentNames.add("<font color='blue'>" + component.getName() + "</font>");
        }
        this.stuckComponentNames.removeAll(this.selectedComponentNames);
        Collections.sort(this.stuckComponentNames);
        refreshStatusText();
    }

}