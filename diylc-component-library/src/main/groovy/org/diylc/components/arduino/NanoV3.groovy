package org.diylc.components.arduino

import org.diylc.components.AbstractComponent
import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.Angle
import org.diylc.components.Colors
import org.diylc.components.AbstractBoard
import org.diylc.components.Geometry
import org.diylc.components.Constants.Placement
import org.diylc.components.Pin;
import org.diylc.components.PinBase;
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.HorizontalAlignment
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Orientation
import org.diylc.core.Project
import org.diylc.core.VerticalAlignment
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.components.ComponentState;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.util.List


@ComponentDescriptor(name = "Arduino Nano V3.0", category = "Arduino", author = "Nikolaj Brinch Jørgensen", zOrder = IDIYComponent.COMPONENT, instanceNamePrefix = "Arduino", description = "Arduino Nano V3.0", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class NanoV3 extends AbstractArduino implements Geometry {

    public static final String id = "6a4967b3-0fc9-4a07-b0a1-12e107401457"
    
    private static final long serialVersionUID = 1L

    private static final int ROW_PIN_COUNT = 15

    private static final int COLUMN_PIN_COUNT = 3

    private static Map<String, List<String>> PCB_TEXT = [
        "row1": [
            "VIN",
            "GND",
            "RST",
            "5V",
            "A7",
            "A6",
            "A5",
            "A4",
            "A3",
            "A2",
            "A2",
            "A1",
            "A0",
            "REF",
            "3V3",
            "D13"
        ],
        "row2": [
            "TX1",
            "RX0",
            "RST",
            "GND",
            "D2",
            "D3",
            "D4",
            "D5",
            "D6",
            "D7",
            "D8",
            "D9",
            "D10",
            "D11",
            "D12"
        ]
    ]

    public NanoV3() {
        super("Nano V3")
    }

    @Override
    protected Area[] getBodyArea() {
        if (body == null) {
            updateControlPoints()
            body = new Area[4]

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
            
            int width = 2 * margin + padSize + ROW_PIN_COUNT * spacing + spacing
            int height = 2 * margin + padSize + ROW_SPACING * spacing

            switch (orientation) {
                case Orientation.DEFAULT:
                    x1 -= spacing
                    break
                case Orientation._90:
                    y1 -= spacing
                case Orientation._270:
                    width = 2 * margin + padSize + ROW_SPACING * spacing
                    height = 2 * margin + padSize + ROW_PIN_COUNT * spacing + spacing
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
                    pX1 -= spacing
                    break
                case Orientation._90:
                    pX1 += (chipBounds.width / 2)
                    pY1 -= (chipBounds.height / 2) + spacing
                    break
                case Orientation._180:
                    pX1 += chipBounds.width + spacing
                    break
                case Orientation._270:
                    pX1 += (chipBounds.width / 2)
                    pY1 += (chipBounds.height / 2) + spacing
                    break
                default:
                    break
            }

            AffineTransform move = AffineTransform.getTranslateInstance(pX1, pY1)
            chipArea.transform(move)

            body[0] = new Area(rectangle(x1, y1, width, height))
            body[1] = chipArea
            body[2] = new Area(new PinBase())
            body[3] = new Area(new Pin())
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
                    dy2 = firstPoint.y + (ROW_SPACING * spacing)
                    row1 = textRow2Reverse
                    row2 = textRow1Reverse
                    row1Placement = Placement.BELOW
                    row2Placement = Placement.ABOVE
                    break
                case Orientation._90:
                    dy1 = firstPoint.y + i * spacing
                    dx2 = firstPoint.x + (ROW_SPACING * spacing)
                    dy2 = firstPoint.y + i * spacing
                    row1 = textRow1Reverse
                    row2 = textRow2Reverse
                    row1Placement = Placement.RIGHT
                    row2Placement = Placement.LEFT
                    break
                case Orientation._180:
                    dx1 = firstPoint.x + i * spacing
                    dx2 = firstPoint.x + i * spacing
                    dy2 = firstPoint.y + (ROW_SPACING * spacing)
                    row1 = textRow1
                    row2 = textRow2
                    row1Placement = Placement.BELOW
                    row2Placement = Placement.ABOVE
                    break
                case Orientation._270:
                    dy1 = firstPoint.y + i * spacing
                    dx2 = firstPoint.x + (ROW_SPACING * spacing)
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
        switch (orientation) {
            case Orientation.DEFAULT:
                int x = firstPoint.x
                int y = firstPoint.y + 2 * spacing

                for (int i = 0; i < COLUMN_PIN_COUNT; i++) {
                    controlPoints << point(x + (ROW_PIN_COUNT * spacing) - spacing, y, ['type': 'pin'])
                    controlPoints << point(x + (ROW_PIN_COUNT * spacing), y, ['type': 'pin'])
                    y += spacing
                }
                break
            case Orientation._90:
                int x = firstPoint.x + 2 * spacing
                int y = firstPoint.y

                for (int i = 0; i < COLUMN_PIN_COUNT; i++) {
                    controlPoints << point(x, y + (ROW_PIN_COUNT * spacing) - spacing, ['type': 'pin'])
                    controlPoints << point(x, y + (ROW_PIN_COUNT * spacing), ['type': 'pin'])
                    x += spacing
                }
                break
            case Orientation._180:
                int x = firstPoint.x - spacing
                int y = firstPoint.y + 2 * spacing

                for (int i = 0; i < COLUMN_PIN_COUNT; i++) {
                    controlPoints << point(x , y, ['type': 'pin'])
                    controlPoints << point(x + spacing , y, ['type': 'pin'])
                    y += spacing
                }
                break
            case Orientation._270:
                int x = firstPoint.x + 2 * spacing
                int y = firstPoint.y - spacing

                for (int i = 0; i < COLUMN_PIN_COUNT; i++) {
                    controlPoints << point(x, y, ['type': 'pin'])
                    controlPoints << point(x, y + spacing, ['type': 'pin'])
                    x += spacing
                }
                break
            default:
                throw new RuntimeException("Unexpected orientation: " + orientation)
        }

        this.controlPoints = controlPoints
    }
}
