package com.kartoflane.superluminal2.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.ui.sidebar.ImagesToolComposite;
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.UIUtils;

public class GibWidget extends Composite {

	private GibController controller = null;

	private Label label = null;
	private Button btnView = null;
	private Button btnBrowse = null;
	private Text txtImage = null;
	private BrowseMenu mnb = null;

	public GibWidget(Composite parent, GibController gib) {
		super(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		controller = gib;

		mnb = new BrowseMenu(this);

		label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		String msg = "Gib #" + gib.getId();
		String alias = gib.getAlias();
		if (alias != null && !alias.equals(""))
			msg += ": " + alias;
		label.setText(msg);

		btnView = new Button(this, SWT.NONE);
		btnView.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnView.setText("View");
		btnView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path = controller.getImage();

				if (path != null) {
					ImageViewerDialog dialog = new ImageViewerDialog(EditorWindow.getInstance().getShell());
					dialog.open(path);
				}
			}
		});

		btnBrowse = new Button(this, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnBrowse.setText("Browse");

		mnb.addTo(btnBrowse);
		mnb.addSystemListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.png" });
				dialog.setFilterPath(ImagesToolComposite.getPrevGibsPath());
				dialog.setFileName(ImagesToolComposite.getPrevGibsPath());

				boolean exit = false;
				while (!exit) {
					String path = dialog.open();

					// path == null only when user cancels
					if (path != null) {
						exit = setImage("file:", path);
					} else {
						exit = true;
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

				boolean exit = false;
				while (!exit) {
					String path = dialog.open();

					// path == null only when user cancels
					if (path == null) {
						exit = true;
					} else {
						exit = setImage("db:", path);
					}
				}
			}
		});

		txtImage = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		txtImage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		String path = controller.getImage();
		txtImage.setText(path == null ? "" : IOUtils.trimProtocol(path));
	}

	public void updateData() {
		String str = "Gib #" + controller.getId();
		String alias = controller.getAlias();
		if (alias != null && !alias.equals(""))
			str += ": " + alias;
		label.setText(str);

		str = controller.getImage();
		txtImage.setText(str == null ? "" : IOUtils.trimProtocol(str));
	}

	private boolean setImage(String protocol, String path) {
		if (protocol.equals("file:") && !new File(path).exists()) {
			UIUtils.showWarningDialog(EditorWindow.getInstance().getShell(), null, "The file you have selected does not exist.");
			return false;
		}

		controller.setImage(protocol + path);
		EditorWindow.getInstance().canvasRedraw();
		updateData();
		return true;
	}
}
