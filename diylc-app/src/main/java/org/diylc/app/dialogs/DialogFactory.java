package org.diylc.app.dialogs;

import java.nio.file.Path;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.diylc.app.dialogs.properties.PropertyEditorDialog;
import org.diylc.app.menus.file.OpenDialog;
import org.diylc.app.menus.file.SaveDialog;
import org.diylc.app.menus.help.AboutDialog;
import org.diylc.app.menus.tools.BomDialog;
import org.diylc.app.online.view.LoginDialog;
import org.diylc.app.online.view.NewUserDialog;
import org.diylc.app.online.view.UploadDialog;
import org.diylc.core.BomEntry;
import org.diylc.core.PropertyWrapper;

public class DialogFactory {

    private static DialogFactory instance;

    public static DialogFactory getInstance() {
        if (instance == null) {
            instance = new DialogFactory();
        }
        return instance;
    }

    private JFrame mainFrame;

    private DialogFactory() {
    }

    /**
     * Sets the frame to be used as dialog parent. This should be called prior
     * to any other methods in this class.
     * 
     * @param mainFrame
     */
    public void initialize(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public PropertyEditorDialog createPropertyEditorDialog(List<PropertyWrapper> properties, String title) {
        return new PropertyEditorDialog(mainFrame, properties, title);
    }

    public BomDialog createBomDialog(List<BomEntry> bom) {
        return new BomDialog(mainFrame, bom);
    }

    public Path showOpenDialog(FileFilter fileFilter, Path lastPath, Path initialFile, String defaultExtension, IFileChooserAccessory accessory) {
        return OpenDialog.newInstance(mainFrame, lastPath, initialFile, fileFilter, defaultExtension, accessory).show();
    }

    public Path showSaveDialog(FileFilter fileFilter, Path lastPath, Path initialFile, String defaultExtension, IFileChooserAccessory accessory) {
        return SaveDialog.newInstance(mainFrame, lastPath, initialFile, fileFilter, defaultExtension).show();
    }

    public AboutDialog createAboutDialog(String appName, Icon icon, String version, String author, String url, String mail,
            String htmlContent) {
        return new AboutDialog(mainFrame, appName, icon, version, author, url, mail, htmlContent);
    }

    public NewUserDialog createNewUserDialog() {
        return new NewUserDialog(mainFrame);
    }

    public LoginDialog createLoginDialog() {
        return new LoginDialog(mainFrame);
    }

    public UploadDialog createUploadDialog() {
        return new UploadDialog(mainFrame);
    }

    public ProgressDialog createProgressDialog(String title, String[] buttonCaptions, String description, boolean useProgress) {
        return new ProgressDialog(mainFrame, title, buttonCaptions, description, useProgress);
    }
}
