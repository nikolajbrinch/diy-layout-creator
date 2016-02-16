package org.diylc.components.arduino

import org.diylc.components.AbstractComponent
import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.Angle
import org.diylc.components.Colors
import org.diylc.components.ComponentDescriptor
import org.diylc.components.AbstractBoard
import org.diylc.components.Geometry
import org.diylc.components.Constants.Placement
import org.diylc.core.ComponentState
import org.diylc.core.HorizontalAlignment
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Orientation
import org.diylc.core.Project
import org.diylc.core.VerticalAlignment
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.util.List


@ComponentDescriptor(name = "Arduino Pro Mini", category = "Arduino", author = "Nikolaj Brinch Jørgensen", zOrder = IDIYComponent.COMPONENT, instanceNamePrefix = "Arduino", description = "Arduino ProMini", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public  class ProMini extends AbstractArduino implements Geometry {

    private static final long serialVersionUID = 1L

    private static final int ROW_PIN_COUNT = 12

    private static final int COLUMN_PIN_COUNT = 6

    private static Map<String, List<String>> PCB_TEXT = [
        "row1": [
            "RAW",
            "GND",
            "RST",
            "VCC",
            "A3",
            "A2",
            "A1",
            "A0",
            "13",
            "12",
            "11",
            "10"
        ],
        "row2": [
            "TXD",
            "RXT",
            "RST",
            "GND",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9"
        ],
        "column1" : ["GND", "A6", "A7"],
        "column2" : [
            "DTR",
            "GND",
            "VCC",
            "RXI",
            "TXD",
            "GRN"]
    ]

    public ProMini() {
        super("ProMini")
    }

    @Override
    public Area[] getBodyArea() {
        if (body == null) {
            updateControlPoints()
            body = new Area[2]

            int spacing = (int) PIN_SPACING.convertToPixels()
            int padSize = (int) org.diylc.components.Constants.SMALL_PAD_SIZE.convertToPixels()
            int holeSize = (int) org.diylc.components.Constants.LARGE_HOLE_SIZE.convertToPixels()
            int chipSize = (int) CHIP_SIZE.convertToPixels()
            int margin = (int) new Size(1d, SizeUnit.mm).convertToPixels()

            Point firstPoint = controlPoints[0]

            int x1 = firstPoint.x
            int y1 = firstPoint.y

            controlPoints.each { point ->
                x1 = Math.min(point.x, x1)
                y1 = Math.min(point.y, y1)
            }

            int width = 2 * margin + padSize + ROW_PIN_COUNT * spacing
            int height = 2 * margin + padSize + COLUMN_PIN_COUNT * spacing

            switch (orientation) {
                case Orientation._90:
                case Orientation._270:
                    width = 2 * margin + padSize + COLUMN_PIN_COUNT * spacing
                    height = 2 * margin + padSize + ROW_PIN_COUNT * spacing
                    break
                default:
                    break
            }

            Area chipArea = new Area(rectangle(0, 0, chipSize, chipSize))

            AffineTransform rotate = AffineTransform.getRotateInstance(Angle._45.getAngle(), (int) 0, 0)
            chipArea.transform(rotate)

            x1 = x1 - padSize / 2 - margin
            y1 = y1 - padSize / 2 - margin

            Rectangle chipBounds = chipArea.getBounds()

            int pX1 = x1 + (width - chipBounds.width) / 2
            int pY1 = y1 + (height - chipBounds.height) / 2

            switch (orientation) {
                case Orientation.DEFAULT:
                    pX1 += spacing
                    break
                case Orientation._90:
                    pX1 += (chipBounds.width / 2)
                    pY1 -= (chipBounds.height / 2) - spacing
                    break
                case Orientation._180:
                    pX1 += chipBounds.width - spacing
                    break
                case Orientation._270:
                    pX1 += (chipBounds.width / 2)
                    pY1 += (chipBounds.height / 2) - spacing
                    break
                default:
                    break
            }

            AffineTransform move = AffineTransform.getTranslateInstance(pX1, pY1)
            chipArea.transform(move)

            body[0] = new Area(rectangle(x1, y1, width, height))
            body[1] = chipArea
        }

        return body
    }

    @Override
    void updateControlPoints() {
        int spacing = (int) PIN_SPACING.convertToPixels()

        Point firstPoint = controlPoints[0]

        List<Point> controlPoints = []

        int dx1 = firstPoint.x
        int dy1 = firstPoint.y
        int dx2 = firstPoint.x
        int dy2 = firstPoint.y

        /*
         * Row headers
         */
        String[] textRow1 = PCB_TEXT["row1"]
        String[] textRow2 = PCB_TEXT["row2"]
        String[] textRow1Reverse = PCB_TEXT["row1"].reverse()
        String[] textRow2Reverse = PCB_TEXT["row2"].reverse()

        String[] row1
        String[] row2
        Placement row1Placement = Placement.BELOW
        Placement row2Placement = Placement.BELOW

        for (int i = 0; i < ROW_PIN_COUNT; i++) {
            switch (orientation) {
                case Orientation.DEFAULT:
                    dx1 = firstPoint.x + i * spacing
                    dx2 = firstPoint.x + i * spacing
                    dy2 = firstPoint.y + (COLUMN_PIN_COUNT * spacing)
                    row1 = textRow2Reverse
                    row2 = textRow1Reverse
                    row1Placement = Placement.BELOW
                    row2Placement = Placement.ABOVE
                    break
                case Orientation._90:
                    dy1 = firstPoint.y + i * spacing
                    dx2 = firstPoint.x + (COLUMN_PIN_COUNT * spacing)
                    dy2 = firstPoint.y + i * spacing
                    row1 = textRow1Reverse
                    row2 = textRow2Reverse
                    row1Placement = Placement.RIGHT
                    row2Placement = Placement.LEFT
                    break
                case Orientation._180:
                    dx1 = firstPoint.x + i * spacing
                    dx2 = firstPoint.x + i * spacing
                    dy2 = firstPoint.y + (COLUMN_PIN_COUNT * spacing)
                    row1 = textRow1
                    row2 = textRow2
                    row1Placement = Placement.BELOW
                    row2Placement = Placement.ABOVE
                    break
                case Orientation._270:
                    dy1 = firstPoint.y + i * spacing
                    dx2 = firstPoint.x + (COLUMN_PIN_COUNT * spacing)
                    dy2 = firstPoint.y + i * spacing
                    row1 = textRow2
                    row2 = textRow1
                    row1Placement = Placement.RIGHT
                    row2Placement = Placement.LEFT
                    break
                default:
                    throw new RuntimeException("Unexpected orientation: " + orientation)
            }

            controlPoints << point(dx1, dy1, ['type': 'pad', 'text': row1[i], 'text-placement': row1Placement])
            controlPoints << point(dx2, dy2, ['type': 'pad', 'text': row2[i], 'text-placement': row2Placement])
        }

        /*
         * Other pins
         */
        String[] column2 = PCB_TEXT["column2"]
        String[] column2Reverse = PCB_TEXT["column2"].reverse()

        switch (orientation) {
            case Orientation.DEFAULT:
                int x = firstPoint.x
                int y = firstPoint.y + spacing / 2

                for (int i = 0; i < COLUMN_PIN_COUNT; i++) {
                    Placement placement = (i == 0) ? Placement.ABOVE : ((i == 5) ? Placement.BELOW : Placement.LEFT)
                    controlPoints << point(x + (ROW_PIN_COUNT * spacing), y, ['type': 'pad', 'text': column2[i], 'text-placement': placement])
                    if (i > 1 && i < 5) {
                        controlPoints << point(x, y, ['type': 'pad', 'text': PCB_TEXT["column1"][i - 2], 'text-placement': Placement.RIGHT])
                    }
                    if (i == 5) {
                        controlPoints << point(x + (7 * spacing) + spacing / 2, y - spacing / 2, ['type': 'pad', 'text': 'A4', 'text-placement': Placement.ABOVE])
                        controlPoints << point(x + (COLUMN_PIN_COUNT * spacing) + spacing / 2, y - spacing / 2, ['type': 'pad', 'text': 'A5', 'text-placement': Placement.ABOVE])
                    }
                    y += spacing
                }
                break
            case Orientation._90:
                int x = firstPoint.x + spacing / 2
                int y = firstPoint.y

                for (int i = 0; i < COLUMN_PIN_COUNT; i++) {
                    Placement placement = (i == 0) ? Placement.LEFT : ((i == 5) ? Placement.RIGHT : Placement.ABOVE)
                    controlPoints << point(x, y + (ROW_PIN_COUNT * spacing), ['type': 'pad', 'text': column2Reverse[i], 'text-placement': placement])
                    if (i > 0 && i < 4) {
                        controlPoints << point(x, y, ['type': 'pad', 'text': PCB_TEXT["column1"][2 - (i - 1)], 'text-placement': Placement.BELOW])
                    }
                    if (i == 1) {
                        controlPoints << point(x - spacing / 2, y + (COLUMN_PIN_COUNT * spacing) + spacing / 2, ['type': 'pad', 'text': 'A5', 'text-placement': Placement.RIGHT])
                        controlPoints << point(x - spacing / 2, y + (7 * spacing) + spacing / 2, ['type': 'pad', 'text': 'A4', 'text-placement': Placement.RIGHT])
                    }
                    x += spacing
                }
                break
            case Orientation._180:
                int x = firstPoint.x - spacing
                int y = firstPoint.y + spacing / 2

                for (int i = 0; i < COLUMN_PIN_COUNT; i++) {
                    Placement placement = (i == 0) ? Placement.ABOVE : ((i == 5) ? Placement.BELOW : Placement.RIGHT) 
                    controlPoints << point(x, y, ['type': 'pad', 'text': column2Reverse[i], 'text-placement': placement])
                    if (i > 0 && i < 4) {
                        controlPoints << point(x + (ROW_PIN_COUNT * spacing), y, ['type': 'pad', 'text': PCB_TEXT["column1"][i - 1], 'text-placement': Placement.LEFT])
                    }
                    if (i == 1) {
                        controlPoints << point(x + (4 * spacing) + spacing / 2, y - spacing / 2, ['type': 'pad', 'text': 'A5', 'text-placement': Placement.BELOW])
                        controlPoints << point(x + (5 * spacing) + spacing / 2, y - spacing / 2, ['type': 'pad', 'text': 'A4', 'text-placement': Placement.BELOW])
                    }
                    y += spacing
                }
                break

            case Orientation._270:
                int x = firstPoint.x + spacing / 2
                int y = firstPoint.y - spacing

                for (int i = 0; i < COLUMN_PIN_COUNT; i++) {
                    Placement placement = (i == 0) ? Placement.LEFT : ((i == 5) ? Placement.RIGHT : Placement.BELOW)
                    controlPoints << point(x, y, ['type': 'pad', 'text': column2[i], 'text-placement': placement])
                    if (i > 1 && i < 5) {
                        controlPoints << point(x, y + (ROW_PIN_COUNT * spacing), ['type': 'pad', 'text': PCB_TEXT["column1"][i - 2], 'text-placement': Placement.ABOVE])
                    }
                    if (i == 5) {
                        controlPoints << point(x - spacing / 2, y + (4 * spacing) + spacing / 2, ['type': 'pad', 'text': 'A5', 'text-placement': Placement.LEFT])
                        controlPoints << point(x - spacing / 2, y + (5 * spacing) + spacing / 2, ['type': 'pad', 'text': 'A4', 'text-placement': Placement.LEFT])
                    }
                    x += spacing
                }
                break
            default:
                throw new RuntimeException("Unexpected orientation: " + orientation)
        }

        this.controlPoints = controlPoints
    }

}
