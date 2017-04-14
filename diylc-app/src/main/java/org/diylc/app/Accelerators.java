package org.diylc.app;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.diylc.core.utils.SystemUtils;

/**
 * @author nikolajbrinch@gmail.com
 */
public class Accelerators {

  public static final KeyStroke ROTATE_RIGHT =
      KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK);

  public static final KeyStroke ROTATE_LEFT =
      KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK);

  public static final KeyStroke SEND_TO_BACK;

  public static final KeyStroke BRING_TO_FRONT;

  public static final KeyStroke GROUP;

  public static final KeyStroke UNGROUP;

  public static final KeyStroke NEW =
      KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke OPEN =
      KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke CLOSE =
      KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke SAVE =
      KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke SAVE_AS =
      KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
          | ActionEvent.SHIFT_MASK);

  public static final KeyStroke PRINT =
      KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke IMPORT =
      KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke UNDO =
      KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke REDO;

  public static final KeyStroke CUT =
      KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke COPY =
      KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke PASTE =
      KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke SELECT_ALL =
      KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke DELETE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

  public static final KeyStroke EDIT_SELECTION =
      KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  public static final KeyStroke SHOW_PROPERTY_PANEL = KeyStroke.getKeyStroke(KeyEvent.VK_E,
      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK);

  public static final KeyStroke MINIMIZE =
      KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

  static {
    if (SystemUtils.isMac()) {
      SEND_TO_BACK =
          KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
              | ActionEvent.SHIFT_MASK);
      BRING_TO_FRONT =
          KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
              | ActionEvent.SHIFT_MASK);
      GROUP = KeyStroke.getKeyStroke(KeyEvent.VK_G,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK);
      UNGROUP = KeyStroke.getKeyStroke(KeyEvent.VK_U,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK);
      REDO = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK);
    } else {
      SEND_TO_BACK = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ActionEvent.ALT_MASK);
      BRING_TO_FRONT = KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.ALT_MASK);
      GROUP = KeyStroke.getKeyStroke(KeyEvent.VK_G,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
      UNGROUP = KeyStroke.getKeyStroke(KeyEvent.VK_U,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
      REDO = KeyStroke.getKeyStroke(KeyEvent.VK_Y,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }
  }

}
