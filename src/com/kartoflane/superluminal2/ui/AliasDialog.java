package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.interfaces.Alias;

public class AliasDialog {
	private Alias alias;

	private Shell shell;
	private Text aliasText;

	private static AliasDialog instance;

	public AliasDialog(Shell parent, Alias alias) {
		this.alias = alias;
		instance = this;

		shell = new Shell(parent, SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Set Alias");
		GridLayout gl_shell = new GridLayout(2, false);
		shell.setLayout(gl_shell);

		aliasText = new Text(shell, SWT.BORDER);
		aliasText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		aliasText.setTextLimit(32);

		final Button btnOk = new Button(shell, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnOk.widthHint = 80;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("OK");

		final Button btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		aliasText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				btnOk.setEnabled(aliasText.getText().length() > 0);
			}
		});

		aliasText.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN && btnOk.isEnabled())
					btnOk.notifyListeners(SWT.Selection, null);
			}
		});

		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AliasDialog.this.alias.setAlias(aliasText.getText());
				shell.dispose();
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});

		shell.pack();
	}

	public void open() {
		Composite parent = shell.getParent();
		Display display = parent.getDisplay();

		Point pLoc = parent.getLocation();
		Point pSize = parent.getSize();
		Point size = shell.getSize();
		shell.setLocation(pLoc.x + pSize.x / 2 - size.x / 2, pLoc.y + pSize.y / 2 - size.y / 2);

		String text = alias.getAlias();
		aliasText.setText(text == null ? "" : text);
		aliasText.selectAll();

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public static AliasDialog getInstance() {
		return instance;
	}

	public boolean isVisible() {
		return !shell.isDisposed() && shell.isVisible();
	}
}
