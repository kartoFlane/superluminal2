package com.kartoflane.superluminal2.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.GlowSet.Glows;
import com.kartoflane.superluminal2.utils.UIUtils;

public class GlowSetDialog {

	private static GlowSetDialog instance = null;
	private static String prevPath = System.getProperty("user.home");

	private boolean typeCloak = false;
	private GlowSet result = null;

	private Button tempSource = null;

	private Shell shell = null;
	private Text txtBlue;
	private Button btnClearYellow;
	private Button btnBrowseYellow;
	private Button btnViewYellow;
	private Button btnClearGreen;
	private Button btnBrowseGreen;
	private Button btnViewGreen;
	private Button btnClearBlue;
	private Button btnBrowseBlue;
	private Button btnViewBlue;
	private Text txtGreen;
	private Text txtYellow;
	private Text txtName;
	private Button btnCancel;
	private Button btnConfirm;
	private Button btnMan;
	private Button btnCloak;
	private Label lblBlue;
	private BrowseMenu mnb;

	public GlowSetDialog(Shell parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Add New Glow Set");
		shell.setLayout(new GridLayout(4, false));

		Group grpType = new Group(shell, SWT.NONE);
		grpType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		grpType.setText("Type");
		grpType.setLayout(new GridLayout(2, false));

		btnMan = new Button(grpType, SWT.RADIO);
		btnMan.setSelection(true);
		btnMan.setText("Manning Glow");

		btnCloak = new Button(grpType, SWT.RADIO);
		btnCloak.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnCloak.setText("Cloaking Glow");

		btnCloak.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				typeCloak = true;
				updateWidgets();
				checkConfirm();
			}
		});

		Label lblNamespace = new Label(shell, SWT.NONE);
		lblNamespace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNamespace.setText("Namespace:");

		txtName = new Text(shell, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		txtName.setText("");

		lblBlue = new Label(shell, SWT.NONE);
		lblBlue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblBlue.setText("Unskilled:");

		btnViewBlue = new Button(shell, SWT.NONE);
		btnViewBlue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnViewBlue.setText("View");

		btnBrowseBlue = new Button(shell, SWT.NONE);
		btnBrowseBlue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnBrowseBlue.setText("Browse");

		btnClearBlue = new Button(shell, SWT.NONE);
		btnClearBlue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClearBlue.setText("Clear");

		txtBlue = new Text(shell, SWT.BORDER);
		txtBlue.setEditable(false);
		txtBlue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		txtBlue.setText("");

		Label lblGreen = new Label(shell, SWT.NONE);
		lblGreen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblGreen.setText("1st Level:");

		btnViewGreen = new Button(shell, SWT.NONE);
		btnViewGreen.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnViewGreen.setText("View");

		btnBrowseGreen = new Button(shell, SWT.NONE);
		btnBrowseGreen.setText("Browse");

		btnClearGreen = new Button(shell, SWT.NONE);
		btnClearGreen.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClearGreen.setText("Clear");

		txtGreen = new Text(shell, SWT.BORDER);
		txtGreen.setEditable(false);
		txtGreen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		txtGreen.setText("");

		Label lblYellow = new Label(shell, SWT.NONE);
		lblYellow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblYellow.setText("2nd Level:");

		btnViewYellow = new Button(shell, SWT.NONE);
		btnViewYellow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnViewYellow.setText("View");

		btnBrowseYellow = new Button(shell, SWT.NONE);
		btnBrowseYellow.setText("Browse");

		btnClearYellow = new Button(shell, SWT.NONE);
		btnClearYellow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClearYellow.setText("Clear");

		txtYellow = new Text(shell, SWT.BORDER);
		txtYellow.setEditable(false);
		txtYellow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		txtYellow.setText("");

		Composite compButtons = new Composite(shell, SWT.NONE);
		GridLayout gl_compButtons = new GridLayout(2, false);
		gl_compButtons.marginHeight = 0;
		gl_compButtons.marginWidth = 0;
		compButtons.setLayout(gl_compButtons);
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));

		btnConfirm = new Button(compButtons, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");
		btnConfirm.setEnabled(false);

		btnCancel = new Button(compButtons, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		btnMan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				typeCloak = false;
				updateWidgets();
				checkConfirm();
			}
		});

		txtName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkConfirm();
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dispose();
			}
		});

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = new GlowSet(txtName.getText());
				if (typeCloak) {
					result.setImage(Glows.CLOAK, txtBlue.getText());
				} else {
					result.setImage(Glows.BLUE, txtBlue.getText());
					result.setImage(Glows.GREEN, txtGreen.getText());
					result.setImage(Glows.YELLOW, txtYellow.getText());
				}
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

		SelectionAdapter imageViewListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Text widget = null;

				if (e.getSource() == btnViewBlue) {
					widget = txtBlue;
				} else if (e.getSource() == btnViewGreen) {
					widget = txtGreen;
				} else if (e.getSource() == btnViewYellow) {
					widget = txtYellow;
				}

				String path = widget.getText();
				if (path == null)
					return;

				File file = new File(path);
				if (file.exists()) {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop != null) {
							try {
								desktop.open(file.getParentFile());
							} catch (IOException ex) {
							}
						}
					} else
						Superluminal.log.error("Unable to open file location - AWT Desktop not supported.");
				}
			}
		};
		btnViewBlue.addSelectionListener(imageViewListener);
		btnViewGreen.addSelectionListener(imageViewListener);
		btnViewYellow.addSelectionListener(imageViewListener);

		mnb = new BrowseMenu(shell);
		SelectionListener browseListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tempSource = (Button) e.getSource();

				mnb.setLocation(tempSource.toDisplay(0, tempSource.getSize().y));
				mnb.setVisible(true);
			}
		};
		btnBrowseBlue.addSelectionListener(browseListener);
		btnBrowseGreen.addSelectionListener(browseListener);
		btnBrowseYellow.addSelectionListener(browseListener);

		mnb.addSystemListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.png" });
				dialog.setFilterPath(prevPath);
				dialog.setFileName(prevPath);

				Text widget = null;

				if (tempSource == btnBrowseBlue) {
					widget = txtBlue;
				} else if (tempSource == btnBrowseGreen) {
					widget = txtGreen;
				} else if (tempSource == btnBrowseYellow) {
					widget = txtYellow;
				}

				boolean exit = false;
				while (!exit) {
					String path = dialog.open();

					// path == null only when user cancels
					if (path == null) {
						exit = true;
					} else {
						prevPath = path;
						File temp = new File(path);
						if (temp.exists()) {
							widget.setText("file:" + path);
							widget.selectAll();
							widget.clearSelection();
							updateWidgets();
							checkConfirm();
							exit = true;
						} else {
							UIUtils.showWarningDialog(EditorWindow.getInstance().getShell(), null, "The file you have selected does not exist.");
						}
					}
				}
			}
		});

		mnb.addDataListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DatabaseFileDialog dialog = new DatabaseFileDialog(EditorWindow.getInstance().getShell());
				dialog.setFilterExtensions(new String[] { "*.png" });
				dialog.setText("FTL Archive Browser");

				Text widget = null;

				if (tempSource == btnBrowseBlue) {
					widget = txtBlue;
				} else if (tempSource == btnBrowseGreen) {
					widget = txtGreen;
				} else if (tempSource == btnBrowseYellow) {
					widget = txtYellow;
				}

				boolean exit = false;
				while (!exit) {
					String path = dialog.open();

					// path == null only when user cancels
					if (path == null) {
						exit = true;
					} else {
						widget.setText("db:" + path);
						widget.selectAll();
						widget.clearSelection();
						updateWidgets();
						checkConfirm();
						exit = true;
					}
				}

			}
		});

		SelectionAdapter imageClearListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Text widget = null;

				if (e.getSource() == btnClearBlue) {
					widget = txtBlue;
				} else if (e.getSource() == btnClearGreen) {
					widget = txtGreen;
				} else if (e.getSource() == btnClearYellow) {
					widget = txtYellow;
				}

				widget.setText("");
				updateWidgets();
				checkConfirm();
			}
		};
		btnClearBlue.addSelectionListener(imageClearListener);
		btnClearGreen.addSelectionListener(imageClearListener);
		btnClearYellow.addSelectionListener(imageClearListener);

		updateWidgets();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		shell.setSize(300, 300);
	}

	public static GlowSetDialog getInstance() {
		return instance;
	}

	public void dispose() {
		shell.dispose();
		instance = null;
	}

	public GlowSet open() {
		result = null;

		shell.open();

		Display display = UIUtils.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return result;
	}

	private void updateWidgets() {
		if (typeCloak) {
			lblBlue.setText("Activation:");
			btnViewGreen.setEnabled(false);
			btnBrowseGreen.setEnabled(false);
			btnClearGreen.setEnabled(false);
			btnViewYellow.setEnabled(false);
			btnBrowseYellow.setEnabled(false);
			btnClearYellow.setEnabled(false);
		} else {
			lblBlue.setText("Unskilled:");
			btnViewGreen.setEnabled(true);
			btnBrowseGreen.setEnabled(true);
			btnClearGreen.setEnabled(true);
			btnViewYellow.setEnabled(true);
			btnBrowseYellow.setEnabled(true);
			btnClearYellow.setEnabled(true);
		}
	}

	private void checkConfirm() {
		boolean result = true;

		String text = txtName.getText();
		result &= text != null && !text.trim().equals("");
		for (GlowSet set : Database.getInstance().getGlowSets())
			result &= !set.getIdentifier().equalsIgnoreCase(text);

		text = txtBlue.getText();
		result &= text != null && !text.trim().equals("");
		if (!typeCloak) {
			text = txtGreen.getText();
			result &= text != null && !text.trim().equals("");
			text = txtYellow.getText();
			result &= text != null && !text.trim().equals("");
		}

		btnConfirm.setEnabled(result);
	}
}
