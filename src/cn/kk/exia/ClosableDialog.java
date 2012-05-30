package cn.kk.exia;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class ClosableDialog extends JDialog {
	private static final long	serialVersionUID	= -4682747886190477495L;

	public ClosableDialog() {
		this((Frame) null, false);
	}

	public ClosableDialog(Frame owner) {
		this(owner, false);
	}

	public ClosableDialog(Frame owner, boolean modal) {
		this(owner, null, modal);
	}

	public ClosableDialog(Frame owner, String title) {
		this(owner, title, false);
	}

	public ClosableDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public ClosableDialog(Dialog owner) {
		this(owner, false);
	}

	public ClosableDialog(Dialog owner, boolean modal) {
		this(owner, null, modal);
	}

	public ClosableDialog(Dialog owner, String title) {
		this(owner, title, false);
	}

	public ClosableDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	protected JRootPane createRootPane() {
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		};
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}
}
