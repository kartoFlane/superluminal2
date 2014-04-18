package com.kartoflane.superluminal2.ui;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.core.Cache;

public class AboutDialog {
	private static AboutDialog instance;

	private String message = "";
	private String linkFaceText = "";
	private URL linkURL = null;

	private Color color = null;

	private Shell shell;
	private Link linkWidget;
	private Label lblText;
	private Composite container;

	public AboutDialog(Shell parent) {
		instance = this;

		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - About");
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		gl_shell.horizontalSpacing = 0;
		shell.setLayout(gl_shell);

		color = Cache.checkOutColor(this, new RGB(255, 255, 255));

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBackground(color);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 10;
		gl_composite.marginHeight = 10;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblIcon = new Label(composite, SWT.NONE);
		lblIcon.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 2));
		lblIcon.setImage(display.getSystemImage(SWT.ICON_INFORMATION));
		lblIcon.setBackground(color);

		lblText = new Label(composite, SWT.NONE);
		lblText.setBackground(color);
		lblText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		linkWidget = new Link(composite, SWT.NONE);
		linkWidget.setBackground(color);
		linkWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		container = new Composite(shell, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Button btnOk = new Button(container, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnOk.widthHint = 80;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("OK");

		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
	}

	public int open() {
		Composite parent = shell.getParent();
		Display display = parent.getDisplay();

		lblText.setText(message);

		String link = "";
		if (linkURL != null) {
			link = linkURL.toString();
			linkWidget.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Program.launch(linkURL.toString());
				}
			});
		}
		linkWidget.setText("<a href=\"" + link + "\">" + linkFaceText + "</a>");

		shell.pack();

		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		Cache.checkInColor(this, color.getRGB());

		return SWT.OK;
	}

	public boolean isVisible() {
		return !shell.isDisposed() && shell.isVisible();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String string) {
		if (string == null)
			throw new NullPointerException("Message cannot be null.");
		message = string;
	}

	public void setLink(URL link, String faceText) {
		if (link == null)
			throw new NullPointerException("URL cannot be null.");
		if (faceText == null)
			throw new NullPointerException("Face text cannot be null.");
		linkFaceText = faceText;
		linkURL = link;
	}

	public static AboutDialog getInstance() {
		return instance;
	}
}
