package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.Superluminal;

public class LoadingDialog {

	private static LoadingDialog instance = null;

	private volatile boolean exit = false;

	private Shell shell = null;
	private Display display = null;

	public LoadingDialog(Shell parentShell, String title, String message) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;
		display = Display.getCurrent();

		if (title == null)
			title = Superluminal.APP_NAME + " - Loading...";
		if (message == null)
			message = "Loading, please wait...";

		shell = new Shell(parentShell, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText(title);
		shell.setLayout(new GridLayout(1, false));

		Label lblLoadingPleaseWait = new Label(shell, SWT.WRAP);
		lblLoadingPleaseWait.setText(message);
		GridData gd_lblLoadingPleaseWait = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lblLoadingPleaseWait.widthHint = 250;
		lblLoadingPleaseWait.setLayoutData(gd_lblLoadingPleaseWait);

		ProgressBar progressBar = new ProgressBar(shell, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				e.doit = false;
			}
		});

		shell.pack();

		Point size = shell.getSize();
		Point parSize = parentShell.getSize();
		Point parLoc = parentShell.getLocation();
		shell.setLocation(parLoc.x + parSize.x / 2 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2);
	}

	public void open() {
		shell.open();

		while (!exit) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		shell.dispose();
	}

	public static LoadingDialog getInstance() {
		return instance;
	}

	public boolean isActive() {
		return !shell.isDisposed() && shell.isVisible();
	}

	public void dispose() {
		exit = true;
		// Wakes the display from sleep
		// Without this call, the dialog would wait for user input, and then disappear.
		display.asyncExec(null);
		instance = null;
	}
}
