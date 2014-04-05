package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Spinner;

public class ManipulationToolComposite extends Composite implements SidebarComposite, DataComposite {
	private Button btnPinned;

	private Composite boundsContainer;
	private Composite dataContainer;

	private AbstractController controller;
	private Button btnUp;
	private Button btnLeft;
	private Button btnRight;
	private Button btnDown;
	private Spinner spNudge;

	public ManipulationToolComposite(Composite parent, boolean location, boolean size) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		label.setText("Manipulation Tool");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		boundsContainer = new Composite(this, SWT.BORDER);
		GridLayout gl_boundsContainer = new GridLayout(3, false);
		boundsContainer.setLayout(gl_boundsContainer);
		boundsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		btnPinned = new Button(boundsContainer, SWT.CHECK);
		btnPinned.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnPinned.setText("Pinned");

		btnUp = new Button(boundsContainer, SWT.NONE);
		btnUp.setImage(SWTResourceManager.getImage(ManipulationToolComposite.class, "/assets/up.png"));
		GridData gd_btnUp = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		gd_btnUp.heightHint = 36;
		gd_btnUp.widthHint = 36;
		btnUp.setLayoutData(gd_btnUp);

		btnLeft = new Button(boundsContainer, SWT.NONE);
		btnLeft.setImage(SWTResourceManager.getImage(ManipulationToolComposite.class, "/assets/left.png"));
		GridData gd_btnLeft = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnLeft.heightHint = 36;
		gd_btnLeft.widthHint = 36;
		btnLeft.setLayoutData(gd_btnLeft);

		spNudge = new Spinner(boundsContainer, SWT.BORDER);
		spNudge.setMinimum(1);

		btnRight = new Button(boundsContainer, SWT.NONE);
		btnRight.setImage(SWTResourceManager.getImage(ManipulationToolComposite.class, "/assets/right.png"));
		GridData gd_btnRight = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnRight.widthHint = 36;
		gd_btnRight.heightHint = 36;
		btnRight.setLayoutData(gd_btnRight);

		btnDown = new Button(boundsContainer, SWT.NONE);
		btnDown.setImage(SWTResourceManager.getImage(ManipulationToolComposite.class, "/assets/down.png"));
		GridData gd_btnDown = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		gd_btnDown.widthHint = 36;
		gd_btnDown.heightHint = 36;
		btnDown.setLayoutData(gd_btnDown);

		dataContainer = new Composite(this, SWT.BORDER);
		dataContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		dataContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		btnPinned.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setPinned(btnPinned.getSelection());
				updateData();
			}
		});

		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				Point p = controller.getPresentedLocation();
				controller.setPresentedLocation(p.x, p.y - spNudge.getSelection());
				updateController();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
			}
		});

		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				Point p = controller.getPresentedLocation();
				controller.setPresentedLocation(p.x, p.y + spNudge.getSelection());
				updateController();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
			}
		});

		btnLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				Point p = controller.getPresentedLocation();
				controller.setPresentedLocation(p.x - spNudge.getSelection(), p.y);
				updateController();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
			}
		});

		btnRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				Point p = controller.getPresentedLocation();
				controller.setPresentedLocation(p.x + spNudge.getSelection(), p.y);
				updateController();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
			}
		});

		setEnabled(false);
	}

	private void updateController() {
		controller.updateFollowOffset();
		Manager.getCurrentShip().updateBoundingArea();
		controller.updateView();
		controller.redraw();
	}

	@Override
	public void setController(AbstractController controller) {
		if (controller == null) {
			btnPinned.setSelection(false);

			Composite c = (Composite) getDataComposite();
			if (c != null)
				c.dispose();

			spNudge.setSelection(1);
			setEnabled(false);
		} else {
			setEnabled(true);

			if (this.controller != null && this.controller.getClass().equals(controller.getClass())) {
				// if the previously selected object is of the same type as the newly
				// selected one, don't dispose the composite -- just update it with new data
				DataComposite dc = getDataComposite();
				dc.setController(controller);
				dc.updateData();
			} else {
				// if they are of different types, or no object was previously selected,
				// create a new composite and insert it into the data container
				Composite c = getComposite();
				if (c != null)
					c.dispose();

				c = (Composite) controller.getDataComposite(dataContainer);
				if (c != null) {
					Control[] changed = { c };
					dataContainer.layout(changed);
				}
			}
		}

		this.controller = controller;
		updateData();
		EditorWindow.getInstance().updateSidebarScroll();
	}

	@Override
	public DataComposite getDataComposite() {
		if (dataContainer.getChildren().length == 0)
			return null;
		else
			return (DataComposite) dataContainer.getChildren()[0];
	}

	@Override
	public Composite getComposite() {
		if (dataContainer.getChildren().length == 0)
			return null;
		else
			return (Composite) dataContainer.getChildren()[0];
	}

	public void updateData() {
		if (controller == null)
			return;

		btnPinned.setSelection(controller.isPinned());
		btnUp.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		btnLeft.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		btnDown.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		btnRight.setEnabled(!controller.isPinned() && controller.isLocModifiable());
		spNudge.setEnabled(!controller.isPinned() && controller.isLocModifiable());
	}

	@Override
	public void setEnabled(boolean enable) {
		btnPinned.setEnabled(enable);
		btnUp.setEnabled(enable);
		btnLeft.setEnabled(enable);
		btnDown.setEnabled(enable);
		btnRight.setEnabled(enable);
		spNudge.setEnabled(enable);
	}

	@Override
	public boolean isFocusControl() {
		boolean result = btnUp.isFocusControl() || btnDown.isFocusControl();
		result |= btnLeft.isFocusControl() || btnRight.isFocusControl();
		return result;
	}
}
