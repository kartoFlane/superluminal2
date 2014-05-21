package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;

public class ManipulationToolComposite extends Composite implements DataComposite {
	private Button btnPinned;

	private Composite boundsContainer;
	private Composite dataContainer;

	private AbstractController controller;
	private Button btnUp;
	private Button btnLeft;
	private Button btnRight;
	private Button btnDown;
	private Spinner spNudge;
	private Label lblX;
	private Spinner spX;
	private Label lblY;
	private Spinner spY;

	private boolean dataLoad = false;

	public ManipulationToolComposite(Composite parent, boolean location, boolean size) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));

		Label label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		label.setText("Manipulation Tool");

		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");
		Label lblHelp = new Label(this, SWT.NONE);
		lblHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHelp.setImage(helpImage);
		String msg = "- Left-click on a highlighted object to select it.\n" +
				"- Left-click on empty space to deselect.\n" +
				"- Only one object can be selected at a time.";
		lblHelp.setToolTipText(msg);

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		boundsContainer = new Composite(this, SWT.BORDER);
		GridLayout gl_boundsContainer = new GridLayout(3, false);
		boundsContainer.setLayout(gl_boundsContainer);
		boundsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		btnPinned = new Button(boundsContainer, SWT.CHECK);
		btnPinned.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnPinned.setText("Pinned");

		lblX = new Label(boundsContainer, SWT.NONE);
		lblX.setText("X:");

		spX = new Spinner(boundsContainer, SWT.BORDER);
		GridData gd_spX = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
		gd_spX.widthHint = 35;
		spX.setLayoutData(gd_spX);
		spX.setEnabled(false);
		spX.setTextLimit(4);
		spX.setMaximum(9999);
		spX.setMinimum(-999);

		lblY = new Label(boundsContainer, SWT.NONE);
		lblY.setText("Y:");

		spY = new Spinner(boundsContainer, SWT.BORDER);
		GridData gd_spY = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
		gd_spY.widthHint = 35;
		spY.setLayoutData(gd_spY);
		spY.setEnabled(false);
		spY.setTextLimit(4);
		spY.setMaximum(9999);
		spY.setMinimum(-999);

		btnUp = new Button(boundsContainer, SWT.NONE);
		btnUp.setImage(Cache.checkOutImage(this, "cpath:/assets/up.png"));
		GridData gd_btnUp = new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1);
		gd_btnUp.heightHint = 36;
		gd_btnUp.widthHint = 36;
		btnUp.setLayoutData(gd_btnUp);

		btnLeft = new Button(boundsContainer, SWT.NONE);
		btnLeft.setImage(Cache.checkOutImage(this, "cpath:/assets/left.png"));
		GridData gd_btnLeft = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnLeft.heightHint = 36;
		gd_btnLeft.widthHint = 36;
		btnLeft.setLayoutData(gd_btnLeft);

		spNudge = new Spinner(boundsContainer, SWT.BORDER);
		GridData gd_spNudge = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_spNudge.widthHint = 20;
		spNudge.setLayoutData(gd_spNudge);
		spNudge.setMinimum(1);
		spNudge.setToolTipText("This determines how much the selected object\nwill move when you press the arrows.");

		btnRight = new Button(boundsContainer, SWT.NONE);
		btnRight.setImage(Cache.checkOutImage(this, "cpath:/assets/right.png"));
		GridData gd_btnRight = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnRight.widthHint = 36;
		gd_btnRight.heightHint = 36;
		btnRight.setLayoutData(gd_btnRight);

		btnDown = new Button(boundsContainer, SWT.NONE);
		btnDown.setImage(Cache.checkOutImage(this, "cpath:/assets/down.png"));
		GridData gd_btnDown = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		gd_btnDown.widthHint = 36;
		gd_btnDown.heightHint = 36;
		btnDown.setLayoutData(gd_btnDown);

		dataContainer = new Composite(this, SWT.BORDER);
		dataContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		dataContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		btnPinned.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setPinned(btnPinned.getSelection());
				updateData();
				EditorWindow.getInstance().forceFocus();
			}
		});

		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				Point p = controller.getPresentedLocation();
				controller.setPresentedLocation(p.x, p.y - spNudge.getSelection());
				updateController();
				updateData();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
				EditorWindow.getInstance().forceFocus();
			}
		});

		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				Point p = controller.getPresentedLocation();
				controller.setPresentedLocation(p.x, p.y + spNudge.getSelection());
				updateController();
				updateData();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
				EditorWindow.getInstance().forceFocus();
			}
		});

		btnLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				Point p = controller.getPresentedLocation();
				controller.setPresentedLocation(p.x - spNudge.getSelection(), p.y);
				updateController();
				updateData();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
				EditorWindow.getInstance().forceFocus();
			}
		});

		btnRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				Point p = controller.getPresentedLocation();
				controller.setPresentedLocation(p.x + spNudge.getSelection(), p.y);
				updateController();
				updateData();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
				EditorWindow.getInstance().forceFocus();
			}
		});

		ModifyListener applyLocListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (!dataLoad)
					applyLocation();
			}
		};

		spX.addModifyListener(applyLocListener);
		spY.addModifyListener(applyLocListener);

		setEnabled(false);
	}

	private void updateController() {
		controller.updateFollowOffset();
		Manager.getCurrentShip().updateBoundingArea();
		controller.updateView();
		controller.redraw();
	}

	public void setController(AbstractController controller) {
		dataLoad = true;

		if (controller == null) {
			btnPinned.setSelection(false);

			Composite c = (Composite) getDataComposite();
			if (c != null)
				c.dispose();

			spX.setSelection(0);
			spY.setSelection(0);
			spNudge.setSelection(1);
			setEnabled(false);
		} else {
			setEnabled(true);

			if (this.controller != null && this.controller.getClass().equals(controller.getClass())) {
				// If the previously selected object is of the same type as the newly
				// selected one, don't dispose the composite -- just update it with new data
				DataComposite dc = getDataComposite();
				dc.setController(controller);
				dc.updateData();
			} else {
				// If they are of different types, or no object was previously selected,
				// create a new composite and insert it into the data container
				Composite c = (Composite) getDataComposite();
				if (c != null)
					c.dispose();

				c = (Composite) controller.getDataComposite(dataContainer);
				if (c != null) {
					Control[] changed = { c };
					dataContainer.layout(changed);
				}
			}
		}

		dataLoad = false;

		this.controller = controller;
		updateData();
		EditorWindow.getInstance().updateSidebarScroll();
	}

	private DataComposite getDataComposite() {
		if (dataContainer.getChildren().length == 0)
			return null;
		else
			return (DataComposite) dataContainer.getChildren()[0];
	}

	public void updateData() {
		if (controller == null)
			return;

		dataLoad = true;

		Point p = controller.getPresentedLocation();
		spX.setSelection(p.x);
		spY.setSelection(p.y);

		btnPinned.setSelection(controller.isPinned());
		spX.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		spY.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		btnUp.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		btnLeft.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		btnDown.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		btnRight.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		spNudge.setEnabled(!controller.isPinned() && controller.isLocModifiable());

		if (getDataComposite() != null)
			getDataComposite().updateData();

		dataLoad = false;
	}

	@Override
	public void setEnabled(boolean enable) {
		btnPinned.setEnabled(enable);
		spX.setEnabled(enable);
		spY.setEnabled(enable);
		btnUp.setEnabled(enable);
		btnLeft.setEnabled(enable);
		btnDown.setEnabled(enable);
		btnRight.setEnabled(enable);
		spNudge.setEnabled(enable);
	}

	private void applyLocation() {
		if (controller != null && controller.isLocModifiable()) {
			Rectangle oldBounds = controller.getBounds();

			controller.setPresentedLocation(spX.getSelection(), spY.getSelection());
			controller.updateFollowOffset();
			controller.updateView();
			Manager.getCurrentShip().updateBoundingArea();

			controller.redraw();
			AbstractController.redraw(oldBounds);
		}
	}

	@Override
	public boolean isFocusControl() {
		boolean result = btnPinned.isFocusControl() || spNudge.isFocusControl();
		result |= spX.isFocusControl() || spY.isFocusControl();
		return result;
	}

	@Override
	public void dispose() {
		super.dispose();
		Cache.checkInImage(this, "cpath:/assets/help.png");
	}
}
