package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.Superluminal;

public class NewShipDialog {

	private static NewShipDialog instance = null;

	private int result = -1;

	private Shell shell;
	private Button btnPlayerShip;
	private Button btnEnemycontrolledShip;
	private Button btnCancel;

	public NewShipDialog(Shell parentShell) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;

		shell = new Shell(parentShell, SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - New Ship");
		shell.setLayout(new GridLayout(2, false));

		Label lblPleaseSelectThe = new Label(shell, SWT.NONE);
		lblPleaseSelectThe.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblPleaseSelectThe.setText("Please select the type of ship you wish to create:");

		btnPlayerShip = new Button(shell, SWT.RADIO);
		btnPlayerShip.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnPlayerShip.setSelection(true);
		btnPlayerShip.setText("Player-Controlled Ship");

		btnEnemycontrolledShip = new Button(shell, SWT.RADIO);
		btnEnemycontrolledShip.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnEnemycontrolledShip.setText("Enemy-Controlled Ship");

		Button btnConfirm = new Button(shell, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");

		btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnPlayerShip.getSelection())
					result = 0;
				else
					result = 1;
				dispose();
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = -1;
				dispose();
			}
		});

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				btnCancel.notifyListeners(SWT.Selection, null);
				e.doit = false;
			}
		});

		shell.pack();
		Point size = shell.getSize();
		shell.setSize(size.x + 5, size.y);
		Point parSize = parentShell.getSize();
		Point parLoc = parentShell.getLocation();
		shell.setLocation(parLoc.x + parSize.x / 3 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2);
	}

	/**
	 * @return 0 if user selected the player ship, 1 if selected enemy ship, or -1 if aborted the dialog.
	 */
	public int open() {
		Display display = Display.getDefault();

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return result;
	}

	public static NewShipDialog getInstance() {
		return instance;
	}

	public void dispose() {
		shell.dispose();
		instance = null;
	}
}
