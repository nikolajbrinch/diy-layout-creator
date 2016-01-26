package org.diylc.app.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.diylc.app.menus.file.DrawingExporter;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(PrintAction.class);

    private static final long serialVersionUID = 1L;

    private IDrawingProvider drawingProvider;

    public PrintAction(IDrawingProvider drawingProvider) {
        super();
        this.drawingProvider = drawingProvider;
        putValue(AbstractAction.NAME, "Print...");
        putValue(AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(AbstractAction.SMALL_ICON, AppIconLoader.Print.getIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info("PrintAction triggered");
        try {
            DrawingExporter.getInstance().print(this.drawingProvider);
        } catch (PrinterException e1) {
            LOG.warn("Error printing", e1);
        }
    }
}