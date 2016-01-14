package org.diylc.components.misc

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.geom.Rectangle2D
import java.util.Iterator
import java.util.List

import org.diylc.components.AbstractComponent
import org.diylc.components.Colors;
import org.diylc.components.Geometry;
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.components.ComponentDescriptor
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.utils.BomEntry
import org.diylc.utils.BomMaker

@ComponentDescriptor(name = "Bill of Materials", author = "Branislav Stojkovic", category = "Misc", description = "", instanceNamePrefix = "BOM", zOrder = IDIYComponent.TEXT, stretchable = false, bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false)
public class BOM extends AbstractComponent<Void> implements Geometry {

    public static Size DEFAULT_SIZE = new Size(10d, SizeUnit.cm)

    public static Size SPACING = new Size(0.1d, SizeUnit.in)

    public static Color COLOR = Color.black

    public static String DEFAULT_TEXT = "No components to show in the Bill of Materials"

    private static final long serialVersionUID = 1L

    private Point point = point(0, 0)

    Void value = null

    @EditableProperty(name = "Width")
    Size size = DEFAULT_SIZE


    @EditableProperty
    Color color = COLOR

    @Override
    public void draw(GraphicsContext graphicContext, ComponentState componentState, boolean outlineMode,
            Project project, IDrawingObserver drawingObserver) {
        List<BomEntry> bom = BomMaker.getInstance().createBom(project.components)
        
        /*
         * Cleanup entries that do not have a value set.
         */
        Iterator<BomEntry> iterator = bom.iterator()
        
        while (iterator.hasNext()) {
            BomEntry entry = iterator.next()

            if (entry.getValue() == null || entry.name.toLowerCase().contains("bom")) {
                iterator.remove()
            }
        }
        
        graphicContext.font = LABEL_FONT
        graphicContext.color = (componentState == ComponentState.DRAGGING || componentState == ComponentState.SELECTED ? Colors.SELECTION_COLOR : getColor())
        
        /*
         * Determine maximum name length and maximum value length to calculate
         * number of columns.
         */
        FontMetrics fontMetrics = graphicContext.fontMetrics
        
        int maxNameWidth = 0
        int maxValueWidth = 0
        int maxHeight = 0
        
        for (BomEntry entry : bom) {
            String valueStr
            
            if (entry.value == null || entry.value.trim().isEmpty()) {
                valueStr = "qty " + entry.getQuantity().toString()
            } else {
                valueStr = entry.value.toString()
            }
            
            Rectangle2D stringBounds = fontMetrics.getStringBounds(entry.getName(), graphicContext.graphics2D)
            
            if (stringBounds.getWidth() > maxNameWidth) {
                maxNameWidth = (int) stringBounds.width
            }
            if (stringBounds.getHeight() > maxHeight) {
                maxHeight = (int) stringBounds.height
            }
            
            stringBounds = fontMetrics.getStringBounds(valueStr, graphicContext.graphics2D)
            if (stringBounds.getWidth() > maxValueWidth) {
                maxValueWidth = (int) stringBounds.width
            }
            
            if (stringBounds.getHeight() > maxHeight) {
                maxHeight = (int) stringBounds.height
            }
        }
        
        /*
         * Calculate maximum entry size.
         */
        int maxEntrySize = maxNameWidth + maxValueWidth + 2 * (int) SPACING.convertToPixels()
        int columnCount = (int) size.convertToPixels() / maxEntrySize
        
        if (columnCount == 0) {
            columnCount = 1
        }
        
        int columnWidth = (int) size.convertToPixels() / columnCount
        int entriesPerColumn = (int) Math.ceil(1d * bom.size() / columnCount)
        if (entriesPerColumn == 0) {
            graphicContext.drawString(DEFAULT_TEXT, point.x, point.y)
            return
        }
        
        for (int i = 0; i < bom.size(); i++) {
            String valueStr
            BomEntry entry = bom.get(i)
            
            if (entry.value == null || entry.value.trim().isEmpty()) {
                valueStr = "qty " + entry.quantity.toString()
            } else {
                valueStr = entry.value.toString()
            }
            
            int columnIndex = i / entriesPerColumn
            int rowIndex = i % entriesPerColumn
            int x = point.x + columnIndex * columnWidth
            int y = point.y + rowIndex * maxHeight
            graphicContext.drawString(entry.name, x, y)
            x += maxNameWidth + SPACING.convertToPixels()
            graphicContext.drawString(valueStr, x, y)
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicContext, int width, int height) {
        graphicContext.color = Color.white
        graphicContext.fillRect(width / 8, 0, 6 * width / 8, height - 1)
        graphicContext.setColor(Color.black)
        graphicContext.drawRect(width / 8, 0, 6 * width / 8, height - 1)
        graphicContext.font = LABEL_FONT.deriveFont(toFloat(9f * width / 32)).deriveFont(Font.PLAIN)

        FontMetrics fontMetrics = graphicContext.fontMetrics
        Rectangle2D rect = fontMetrics.getStringBounds("BOM", graphicContext.graphics2D)

        int textHeight = (int) (rect.height)
        int textWidth = (int) (rect.width)

        // Center text horizontally and vertically.
        int x = (width - textWidth) / 2 + 1
        int y = textHeight + 2

        graphicContext.drawString("BOM", x, y)

        graphicContext.setFont(graphicContext.font.deriveFont(toFloat(1f * 5 * width / 32)))

        fontMetrics = graphicContext.getFontMetrics()
        rect = fontMetrics.getStringBounds("resistors", graphicContext.graphics2D)
        x = (width - textWidth) / 2 + 1
        y = height / 2 + 2
        graphicContext.drawString("resistors", x, y)
        y += rect.getHeight() - 1
        graphicContext.drawString("tubes", x, y)
        y += rect.getHeight() - 1
        graphicContext.drawString("diodes", x, y)
    }

    @Override
    public int getControlPointCount() {
        return 1
    }

    @Override
    public Point getControlPoint(int index) {
        return point
    }

    @Override
    public boolean isControlPointSticky(int index) {
        return false
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.NEVER
    }

    @Override
    public void setControlPoint(Point point, int index) {
        this.point.setLocation(point)
    }

}
