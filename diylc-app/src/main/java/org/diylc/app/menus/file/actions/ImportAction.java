package org.diylc.app.menus.file.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.ITask;
import org.diylc.app.IView;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.menus.file.FileFilterEnum;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.Presenter;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class ImportAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ImportAction.class);

    private static final long serialVersionUID = 1L;

    private IPlugInPort plugInPort;
    
    private ISwingUI swingUI;
    
    private Presenter presenter;

    public ImportAction(IPlugInPort plugInPort, ISwingUI swingUI) {
        super();
        this.plugInPort = plugInPort;
        this.swingUI = swingUI;
        this.presenter = new Presenter(new IView() {

            @Override
            public int showConfirmDialog(String message, String title,
                    int optionType, int messageType) {
                return JOptionPane.showConfirmDialog(null, message, title,
                        optionType, messageType);
            }

            @Override
            public void showMessage(String message, String title,
                    int messageType) {
                JOptionPane.showMessageDialog(null, message, title,
                        messageType);
            }

            @Override
            public Path promptFileSave() {
                return null;
            }

            @Override
            public boolean editProperties(List<PropertyWrapper> properties,
                    Set<PropertyWrapper> defaultedProperties) {
                return false;
            }
        });
        putValue(AbstractAction.NAME, "Import");
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_I, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask()));
        putValue(AbstractAction.SMALL_ICON,
                AppIconLoader.ElementInto.getIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info("ImportAction triggered");

        final Path path = DialogFactory.getInstance().showOpenDialog(
                FileFilterEnum.DIY.getFilter(), Configuration.INSTANCE.getLastPath(), null,
                FileFilterEnum.DIY.getExtensions()[0], null);
        if (path != null) {
            swingUI.executeBackgroundTask(new ITask<Void>() {

                @Override
                public Void doInBackground() throws Exception {
                    LOG.debug("Opening from " + path.toAbsolutePath());
                    // Load project in temp presenter
                    presenter.loadProjectFromFile(path);
                    // Grab all components and paste them into the main
                    // presenter
                    plugInPort.pasteComponents(presenter
                            .getCurrentProject().getComponents());
                    // Cleanup components in the temp presenter, don't need
                    // them anymore
                    presenter.selectAll(0);
                    presenter.deleteSelectedComponents();
                    return null;
                }

                @Override
                public void complete(Void result) {
                }

                @Override
                public void failed(Exception e) {
                    swingUI.showMessage(
                            "Could not open file. " + e.getMessage(),
                            "Error", ISwingUI.ERROR_MESSAGE);
                }
            });
        }
    }
}