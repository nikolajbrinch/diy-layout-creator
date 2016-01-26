package org.diylc.app.menus.edit;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.diylc.app.utils.AppIconLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.util.Utils;

/**
 * Utility that handles undo/redo operations. Use {@link #getUndoAction()} and
 * {@link #getRedoAction()} to obtain instances of actions that may be used for
 * menus, buttons, etc. Action states are updated automatically, i.e. they are
 * enabled and disabled when they need to be.
 * 
 * @author Branislav Stojkovic
 * 
 * @param <T>
 *            type of entities that actions are performed upon
 */
public class UndoHandler<T> {

	private static final Logger LOG = LoggerFactory
			.getLogger(UndoHandler.class);

	public static final int MAX_STACK_SIZE = 32;

	private Stack<Change> undoStack;
	private Stack<Change> redoStack;
	private IUndoListener<T> listener;

	private UndoAction undoAction;
	private RedoAction redoAction;

	public UndoHandler(IUndoListener<T> listener) {
		super();
		this.listener = listener;
		this.undoStack = new Stack<Change>();
		this.redoStack = new Stack<Change>();
		refreshActions();
	}

	public UndoAction getUndoAction() {
		if (undoAction == null) {
			undoAction = new UndoAction();
		}
		return undoAction;
	}

	public RedoAction getRedoAction() {
		if (redoAction == null) {
			redoAction = new RedoAction();
		}
		return redoAction;
	}

	public void reset() {
		LOG.info("Resetting undo/redo");
		undoStack.clear();
		redoStack.clear();
		refreshActions();
	}

	/**
	 * This method is called when entity change occurs.
	 * 
	 * @param previousState
	 * @param currentState
	 * @param changeDescription
	 */
	public void stateChanged(T previousState, T currentState,
			String changeDescription) {
		LOG.info("Undo state changed");
		undoStack.push(new Change(previousState, currentState,
				changeDescription));
		redoStack.clear();
		refreshActions();
	}

	class UndoAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public UndoAction() {
			super();
			putValue(Action.NAME, "Undo");
			putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
							.getMenuShortcutKeyMask()));
			putValue(AbstractAction.SMALL_ICON, AppIconLoader.Undo.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!undoStack.isEmpty()) {
				LOG.info("Performing undo");
				Change currentState = undoStack.pop();
				redoStack.push(currentState);
				listener.actionPerformed(currentState.before);
				refreshActions();
			} else {
				LOG.warn("Could not perform undo");
			}
		}
	}

	class RedoAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RedoAction() {
			super();
			putValue(Action.NAME, "Redo");
			if (Utils.isMac()) {
				putValue(AbstractAction.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit
								.getDefaultToolkit().getMenuShortcutKeyMask()
								| ActionEvent.SHIFT_MASK));
			} else {
				putValue(AbstractAction.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit
								.getDefaultToolkit().getMenuShortcutKeyMask()));
			}
			
			putValue(AbstractAction.SMALL_ICON, AppIconLoader.Redo.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!redoStack.isEmpty()) {
				LOG.info("Performing redo");
				Change currentState = redoStack.pop();
				undoStack.push(currentState);
				listener.actionPerformed(currentState.after);
				refreshActions();
			} else {
				LOG.warn("Could not perform undo");
			}
		}
	}

	private void refreshActions() {
		getUndoAction().setEnabled(!undoStack.isEmpty());
		if (undoStack.isEmpty()) {
			getUndoAction().putValue(Action.NAME, "Undo");
		} else {
			getUndoAction().putValue(Action.NAME,
					"Undo " + undoStack.peek().changeDescription);
		}
		getRedoAction().setEnabled(!redoStack.isEmpty());
		if (redoStack.isEmpty()) {
			getRedoAction().putValue(Action.NAME, "Redo");
		} else {
			getRedoAction().putValue(Action.NAME,
					"Redo " + redoStack.peek().changeDescription);
		}
		while (undoStack.size() > MAX_STACK_SIZE) {
			undoStack.remove(0);
		}
		while (redoStack.size() > MAX_STACK_SIZE) {
			redoStack.remove(0);
		}
	}

	class Change {
		T before;
		T after;
		String changeDescription;

		public Change(T before, T after, String changeDescription) {
			super();
			this.before = before;
			this.after = after;
			this.changeDescription = changeDescription;
		}
	}
}
