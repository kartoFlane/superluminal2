package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.Superluminal;

public class ImageViewerDialog {
	private static ImageViewerDialog instance = null;

	private Shell shell = null;

	public ImageViewerDialog(Shell parent) {
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Image Viewer");
		shell.setLayout(new GridLayout(4, false));
	}

	public static ImageViewerDialog getInstance() {
		return instance;
	}

	public void open() {
	}

	public boolean isVisible() {
		return shell.isVisible();
	}
}
