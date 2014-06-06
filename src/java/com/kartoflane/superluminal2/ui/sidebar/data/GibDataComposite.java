package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.ui.GibControlsMenu;
import com.kartoflane.superluminal2.ui.GibPropContainer;
import com.kartoflane.superluminal2.ui.GibPropContainer.PropControls;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.utils.UIUtils;

public class GibDataComposite extends Composite implements DataComposite {

	private GibController controller = null;
	private GibPropContainer gibContainer = null;

	private Label label = null;
	private Button btnControls;

	public GibDataComposite(Composite parent, GibController control) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));

		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");

		controller = control;
		gibContainer = Manager.getCurrentShip().getGibContainer();

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblShowControls = new Label(this, SWT.NONE);
		lblShowControls.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblShowControls.setText("Show Controls:");

		btnControls = new Button(this, SWT.NONE);
		GridData gd_btnControls = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnControls.widthHint = 100;
		btnControls.setLayoutData(gd_btnControls);

		Label lblControlsHelp = new Label(this, SWT.NONE);
		lblControlsHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblControlsHelp.setImage(helpImage);
		String msg = ""; // TODO
		UIUtils.addTooltip(lblControlsHelp, "", msg);

		btnControls.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipContainer container = Manager.getCurrentShip();
				Point p = btnControls.getLocation();
				GibControlsMenu propMenu = new GibControlsMenu(container.getParent().getShell());
				propMenu.setLocation(toDisplay(p.x, p.y + btnControls.getSize().y));
				PropControls result = propMenu.open();
				if (result != null) {
					container.getGibContainer().showControls(result);
					container.getParent().updateSidebarContent();
					btnControls.setText(result.toString());
				}
			}
		});

		updateData();
	}

	@Override
	public void updateData() {
		String alias = controller.getAlias();
		label.setText("Gib" + (alias == null ? "" : " (" + alias + ")"));

		btnControls.setText(Manager.getCurrentShip().getGibContainer().getShownControls().toString());
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (GibController) controller;
		gibContainer.setCurrentController(this.controller);
		gibContainer.showControls(gibContainer.getShownControls());
	}

	public void reloadController() {
	}
}
