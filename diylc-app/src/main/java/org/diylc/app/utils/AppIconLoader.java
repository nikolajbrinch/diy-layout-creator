package org.diylc.app.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads image resources as Icons.
 *
 * @author Branislav Stojkovic
 */
public enum AppIconLoader {

    About("about.png"),
    Add("add.png"),
    Back("back.png"),
    BlackBoard("blackboard.png"),
    BOM("bom.png"),
    BranchAdd("branch_add.png"),
    BriefcaseAdd("briefcase_add.png"),
    BriefcaseInto("briefcase_into.png"),
    Bug("bug.png"),
    Chest("chest.png"),
    Component("component.png"),
    Copy("copy.png"),
    CSV("csv.png"),
    Cut("cut.png"),
    Delete("delete.png"),
    DiskBlue("disk_blue.png"),
    DocumentEdit("document_edit.png"),
    DocumentPlainYellow("document_plain_yellow.png"),
    Donate("donate.png"),
    EditComponent("edit_component.png"),
    ElementInto("element_into.png"),
    ElementsSelection("elements_selection.png"),
    Error("error.png"),
    Excel("excel.png"),
    Exit("exit.png"),
    Faq("faq.png"),
    FolderOut("folder_out.png"),
    FormGreen("form_green.png"),
    Front("front.png"),
    Garbage("garbage.png"),
    Gears("gears.png"),
    Group("group.png"),
    HTML("html.png"),
    IconLarge("icon_large.png"),
    IconMedium("icon_medium.png"),
    IconSmall("icon_small.png"),
    IdCard("id_card.png"),
    IdCardAdd("id_card_add.png"),
    Image("image.png"),
    LightBulbOff("lightbulb_off.png"),
    LightBulbOn("lightbulb_on.png"),
    Manual("manual.png"),
    MoveSmall("move_small.png"),
    NavigateCheck("navigate_check.png"),
    NotebookAdd("notebook_add.png"),
    Paste("paste.png"),
    PDF("pdf.png"),
    Pens("pens.png"),
    PhotoScenery("photo_scenery.png"),
    Plugin("plugin.png"),
    Print("print.png"),
    Redo("redo.png"),
    RotateCCW("rotate_ccw.png"),
    RotateCW("rotate_cw.png"),
    SaveAs("save_as.png"),
    Selection("selection.png"),
    Size("size.png"),
    Sort("sort.png"),
    TraceMask("trace_mask.png"),
    Undo("undo.png"),
    Ungroup("ungroup.png"),
    Upload("upload.png"),
    Warning("warning.png"),
    WindowColors("window_colors.png"),
    WindowGear("window_gear.png"),
    Wrench("wrench.png"),
    ZoomSmall("zoom_small.png");

    private static final Logger LOG = LoggerFactory.getLogger(AppIconLoader.class);

    protected String name;

    private AppIconLoader(String name) {
        this.name = name;
    }

    public Icon getIcon() {
        URL imgURL = getClass().getResource("/icons/" + name);
        if (imgURL != null) {
            return new ImageIcon(imgURL, name);
        } else {
            LOG.error("Couldn't find file: " + imgURL);
            return null;
        }
    }

    public Image getImage() {
        BufferedImage img = null;
        try {
            URL imgURL = getClass().getResource("/icons/" + name);
            InputStream imgStream = null;
            try {
                imgStream = imgURL.openStream();
                img = ImageIO.read(imgStream);
            } finally {
                if (imgStream != null) {
                    imgStream.close();
                }
            }
        } catch (IOException e) {
            LOG.error("Couldn't find file: " + name);
        }
        return img;
    }
}
