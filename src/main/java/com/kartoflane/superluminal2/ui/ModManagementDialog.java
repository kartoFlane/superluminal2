package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.widgets.Shell;

public class ModManagementDialog {
	private static ModManagementDialog instance = null;
	private Shell shell;

	public ModManagementDialog(Shell parent) {
		instance = this;
	}

	public static ModManagementDialog getInstance() {
		return instance;
	}
}
