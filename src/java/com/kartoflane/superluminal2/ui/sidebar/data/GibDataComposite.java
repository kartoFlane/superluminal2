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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.enums.OS;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.ui.GibControlsMenu;
import com.kartoflane.superluminal2.ui.GibPropContainer;
import com.kartoflane.superluminal2.ui.GibPropContainer.PropControls;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;

public class GibDataComposite extends Composite implements DataComposite {

	private static boolean valueControlsVisible = false;

	private GibController controller = null;
	private GibPropContainer gibContainer = null;

	private Label label = null;
	private Button btnControls;
	private Spinner spDirMin;
	private Spinner spDirMax;
	private Group grpValueControls;
	private Button btnShow;
	private Spinner spLinMin;
	private Spinner spLinMax;
	private Spinner spAngMin;
	private Spinner spAngMax;

	public GibDataComposite(Composite parent, GibController control) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));

		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");

		controller = control;
		gibContainer = Manager.getCurrentShip().getGibContainer();

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblHelp = new Label(this, SWT.NONE);
		lblHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHelp.setImage(helpImage);
		String msg = "- Click on the controls button to open controls selection menu\n" +
				"- Alternatively, right-click on the gib to open the menu\n" +
				"- You can drag the controls around to modify the selected property";
		UIUtils.addTooltip(lblHelp, msg);

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblShowControls = new Label(this, SWT.NONE);
		lblShowControls.setText("Show Controls:");

		btnControls = new Button(this, SWT.NONE);
		GridData gd_btnControls = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
		gd_btnControls.widthHint = 100;
		btnControls.setLayoutData(gd_btnControls);

		btnShow = new Button(this, SWT.TOGGLE);
		GridData gd_btnShow = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_btnShow.widthHint = 160;
		btnShow.setLayoutData(gd_btnShow);
		btnShow.setSelection(valueControlsVisible);
		if (valueControlsVisible)
			btnShow.setText("Hide Raw Controls");
		else
			btnShow.setText("Show Raw Controls");

		Label lblShowHelp = new Label(this, SWT.NONE);
		lblShowHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblShowHelp.setImage(helpImage);
		msg = "Displays a set of advanced widgets containing raw data, allowing you to " +
				"directly modify each value without having to use the graphical controls.";
		UIUtils.addTooltip(lblShowHelp, Utils.wrapOSNot(msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX()));

		grpValueControls = new Group(this, SWT.NONE);
		grpValueControls.setLayout(new GridLayout(2, false));
		grpValueControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		grpValueControls.setText("Raw Value Controls");

		Label lblDirection = new Label(grpValueControls, SWT.NONE);
		lblDirection.setText("Direction:");

		spDirMin = new Spinner(grpValueControls, SWT.BORDER);
		GridData gd_spDirMin = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_spDirMin.widthHint = 25;
		spDirMin.setLayoutData(gd_spDirMin);
		spDirMin.setMaximum(360);
		spDirMin.setMinimum(-360);

		spDirMax = new Spinner(grpValueControls, SWT.BORDER);
		GridData gd_spDirMax = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
		gd_spDirMax.widthHint = 25;
		spDirMax.setLayoutData(gd_spDirMax);
		spDirMax.setMaximum(360);
		spDirMax.setMinimum(-360);

		Label lblLinear = new Label(grpValueControls, SWT.NONE);
		lblLinear.setText("Linear Velocity:");

		spLinMin = new Spinner(grpValueControls, SWT.BORDER);
		spLinMin.setDigits(2);
		spLinMin.setMaximum(500);
		GridData gd_spLinMin = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_spLinMin.widthHint = 25;
		spLinMin.setLayoutData(gd_spLinMin);

		Label lblLinHelp = new Label(grpValueControls, SWT.NONE);
		lblLinHelp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblLinHelp.setImage(helpImage);
		msg = String.format("1.00 = %s pixels per second%n(Death animation lasts %s seconds)",
				Database.GIB_LINEAR_SPEED, Database.GIB_DEATH_ANIM_TIME);
		UIUtils.addTooltip(lblLinHelp, msg);

		spLinMax = new Spinner(grpValueControls, SWT.BORDER);
		spLinMax.setMaximum(500);
		spLinMax.setDigits(2);
		GridData gd_spLinMax = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_spLinMax.widthHint = 25;
		spLinMax.setLayoutData(gd_spLinMax);

		Label lblAngular = new Label(grpValueControls, SWT.NONE);
		lblAngular.setText("Angular Velocity:");

		spAngMin = new Spinner(grpValueControls, SWT.BORDER);
		spAngMin.setMaximum(500);
		spAngMin.setMinimum(-500);
		spAngMin.setDigits(2);
		GridData gd_spAngMin = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_spAngMin.widthHint = 25;
		spAngMin.setLayoutData(gd_spAngMin);

		Label lblAngHelp = new Label(grpValueControls, SWT.NONE);
		lblAngHelp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblAngHelp.setImage(helpImage);
		msg = String.format("1.00 = %s degrees per second%n(Death animation lasts %s seconds)",
				Math.round(Math.toDegrees(Database.GIB_ANGULAR_SPEED)), Database.GIB_DEATH_ANIM_TIME);
		UIUtils.addTooltip(lblAngHelp, msg);

		spAngMax = new Spinner(grpValueControls, SWT.BORDER);
		spAngMax.setDigits(2);
		spAngMax.setMaximum(500);
		spAngMax.setMinimum(-500);
		GridData gd_spAngMax = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_spAngMax.widthHint = 25;
		spAngMax.setLayoutData(gd_spAngMax);

		btnShow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				valueControlsVisible = btnShow.getSelection();
				grpValueControls.setVisible(valueControlsVisible);
				if (valueControlsVisible)
					btnShow.setText("Hide Raw Controls");
				else
					btnShow.setText("Show Raw Controls");
			}
		});

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

		spDirMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setDirectionMin(spDirMin.getSelection());
				Manager.getCurrentShip().getGibContainer().setCurrentController(controller);
			}
		});
		spDirMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setDirectionMax(spDirMax.getSelection());
				Manager.getCurrentShip().getGibContainer().setCurrentController(controller);
			}
		});

		spLinMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setLinearVelocityMin(spLinMin.getSelection() / 100f);
				Manager.getCurrentShip().getGibContainer().setCurrentController(controller);
				spLinMax.setMinimum(spLinMin.getSelection());
			}
		});
		spLinMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setLinearVelocityMax(spLinMax.getSelection() / 100f);
				Manager.getCurrentShip().getGibContainer().setCurrentController(controller);
				spLinMin.setMaximum(spLinMax.getSelection());
			}
		});

		spAngMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setAngularVelocityMin(spAngMin.getSelection() / 100f);
				Manager.getCurrentShip().getGibContainer().setCurrentController(controller);
			}
		});
		spAngMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setAngularVelocityMax(spAngMax.getSelection() / 100f);
				Manager.getCurrentShip().getGibContainer().setCurrentController(controller);
			}
		});

		grpValueControls.setVisible(valueControlsVisible);
		updateData();
	}

	@Override
	public void updateData() {
		String alias = controller.getAlias();
		label.setText("Gib #" + controller.getId() + (alias == null ? "" : " (" + alias + ")"));

		btnControls.setText(Manager.getCurrentShip().getGibContainer().getShownControls().toString());
		spDirMin.setSelection(controller.getDirectionMin());
		spDirMax.setSelection(controller.getDirectionMax());
		// In SWT, there's no way to pass floating point values to a spinner.
		// Instead, the decimal value is acquired by dividing the input of setSelection()
		// by 10^n, where n is the number of decimal digits ---> Spinner.getDigits()
		// Here we have two decimal digits, hence 10^2 = 100
		spLinMin.setSelection((int) Math.round(controller.getLinearVelocityMin() * 100));
		spLinMax.setSelection((int) Math.round(controller.getLinearVelocityMax() * 100));
		spLinMin.setMaximum(spLinMax.getSelection());
		spLinMax.setMinimum(spLinMin.getSelection());
		spAngMin.setSelection((int) Math.round(controller.getAngularVelocityMin() * 100));
		spAngMax.setSelection((int) Math.round(controller.getAngularVelocityMax() * 100));
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
