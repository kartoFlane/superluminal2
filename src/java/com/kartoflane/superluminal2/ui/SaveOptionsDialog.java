package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.Superluminal;
import org.eclipse.swt.layout.GridLayout;

public class SaveOptionsDialog {
	private static SaveOptionsDialog instance = null;
	private Shell shell = null;

	public SaveOptionsDialog(Shell parent) {
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Save Options Dialog");
		shell.setLayout(new GridLayout(1, false));
	}

	public static SaveOptionsDialog getInstance() {
		return instance;
	}
}
