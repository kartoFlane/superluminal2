package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Spinner;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.ui.EditorWindow;

public class ManipulationToolComposite extends Composite implements SidebarComposite, DataComposite {
	private Button btnPinned;
	private Spinner spX;
	private Spinner spY;
	private Spinner spW;
	private Spinner spH;
	private Label labelX;
	private Label labelY;
	private Label labelW;
	private Label labelH;

	private Composite boundsContainer;
	private Composite dataContainer;

	private boolean locModifiable = true;
	private boolean sizeModifiable = true;

	private boolean dataLoad = false;

	private AbstractController controller;

	public ManipulationToolComposite(Composite parent, boolean location, boolean size) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		label.setText("Manipulation Tool");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		boundsContainer = new Composite(this, SWT.BORDER);
		GridLayout gl_boundsContainer = new GridLayout(2, false);
		boundsContainer.setLayout(gl_boundsContainer);
		boundsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		btnPinned = new Button(boundsContainer, SWT.CHECK);
		btnPinned.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnPinned.setText("Pinned");

		labelX = new Label(boundsContainer, SWT.NONE);
		labelX.setText("X:");

		spX = new Spinner(boundsContainer, SWT.BORDER);
		spX.setTextLimit(4);
		spX.setMaximum(9999);
		spX.setMinimum(-999);
		spX.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		labelY = new Label(boundsContainer, SWT.NONE);
		labelY.setText("Y:");

		spY = new Spinner(boundsContainer, SWT.BORDER);
		spY.setTextLimit(4);
		spY.setMaximum(9999);
		spY.setMinimum(-999);
		spY.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		dataContainer = new Composite(this, SWT.BORDER);
		dataContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		dataContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		setLocationModifiable(location);
		setSizeModifiable(size);

		btnPinned.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setPinned(btnPinned.getSelection());
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

		setData(null);
	}

	/**
	 * Not all entities' size can be modified, so we only create the widgets when it's necessary.
	 */
	private void createSize() {
		labelW = new Label(boundsContainer, SWT.NONE);
		labelW.setText("W:");

		spW = new Spinner(boundsContainer, SWT.BORDER);
		spW.setTextLimit(4);
		spW.setMaximum(9999);
		spW.setMinimum(-999);
		spW.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		labelH = new Label(boundsContainer, SWT.NONE);
		labelH.setText("H:");

		spH = new Spinner(boundsContainer, SWT.BORDER);
		spH.setTextLimit(4);
		spH.setMaximum(9999);
		spH.setMinimum(-999);
		spH.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		ModifyListener applySizeListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (!dataLoad)
					applySize();
			}
		};

		spW.addModifyListener(applySizeListener);
		spH.addModifyListener(applySizeListener);

		Control[] changed = { labelW, spW, labelH, spH };
		boundsContainer.layout(changed);
		pack();
	}

	public void setLocationModifiable(boolean b) {
		locModifiable = b;

		spX.setEnabled(b);
		spY.setEnabled(b);
	}

	public void setSizeModifiable(boolean b) {
		sizeModifiable = b;
		if (b && labelW == null) {
			createSize();
		} else if (!b && labelW != null) {
			labelW.dispose();
			spW.dispose();
			labelH.dispose();
			spH.dispose();

			labelW = null;
			spW = null;
			labelH = null;
			spH = null;

			pack();
		}
	}

	public boolean isLocationModifiable() {
		return locModifiable;
	}

	public boolean isSizeModifiable() {
		return sizeModifiable;
	}

	@Override
	public void setController(AbstractController controller) {
		dataLoad = true;

		if (controller == null) {
			// reset fields

			btnPinned.setSelection(false);

			spX.setSelection(0);
			spY.setSelection(0);

			if (sizeModifiable) {
				spW.setSelection(0);
				spH.setSelection(0);
			}

			Composite c = (Composite) getDataComposite();
			if (c != null)
				c.dispose();

			setEnabled(false);
		} else {
			setEnabled(true);

			btnPinned.setSelection(controller.isPinned());

			setLocationModifiable(controller.getModel().isLocModifiable() && !controller.isPinned());
			setSizeModifiable(controller.getModel().isSizeModifiable() && !controller.isPinned());

			Point p = controller.toPresentedLocation(controller.getLocation());
			spX.setSelection(p.x);
			spY.setSelection(p.y);

			if (controller.getModel().isSizeModifiable()) {
				p = controller.toPresentedSize(controller.getSize());
				spW.setSelection(p.x);
				spH.setSelection(p.y);
			}

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
		EditorWindow.getInstance().updateSidebarScroll();

		dataLoad = false;
	}

	@Override
	public AbstractController getController() {
		return controller;
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

		dataLoad = true;

		btnPinned.setSelection(controller.isPinned());

		setLocationModifiable(controller.getModel().isLocModifiable() && !controller.isPinned());
		setSizeModifiable(controller.getModel().isSizeModifiable() && !controller.isPinned());

		Point p = controller.toPresentedLocation(controller.getLocation());
		spX.setSelection(p.x);
		spY.setSelection(p.y);

		if (sizeModifiable) {
			p = controller.toPresentedSize(controller.getSize());
			spW.setSelection(p.x);
			spH.setSelection(p.y);
		}

		dataLoad = false;
	}

	public void setPresentedLocation(int x, int y) {
		if (locModifiable) {
			spX.setSelection(x);
			spY.setSelection(y);
			applyLocation();
		}
	}

	public Point getPresentedLocation() {
		if (locModifiable)
			return new Point(spX.getSelection(), spY.getSelection());
		else
			return null;
	}

	public void setPresentedSize(int w, int h) {
		if (sizeModifiable) {
			spW.setSelection(w);
			spH.setSelection(h);
			applySize();
		}
	}

	public Point getPresentedSize() {
		if (sizeModifiable)
			return new Point(spW.getSelection(), spH.getSelection());
		else
			return null;
	}

	@Override
	public void setEnabled(boolean enable) {
		super.setEnabled(enable);

		btnPinned.setEnabled(enable);

		setLocationModifiable(controller != null && controller.getModel().isLocModifiable() && !controller.isPinned());
		setSizeModifiable(controller != null && controller.getModel().isSizeModifiable() && !controller.isPinned());
	}

	private void applyLocation() {
		if (controller.getModel().isLocModifiable()) {
			Rectangle oldBounds = controller.getBounds();
			controller.setLocation(controller.toNormalLocation(spX.getSelection(), spY.getSelection()));
			EditorWindow.getInstance().canvasRedraw(controller.getBounds());
			EditorWindow.getInstance().canvasRedraw(oldBounds);
		}
	}

	private void applySize() {
		if (sizeModifiable && controller.getModel().isSizeModifiable()) {
			Rectangle oldBounds = controller.getBounds();
			controller.setSize(controller.toNormalSize(spW.getSelection(), spH.getSelection()));
			EditorWindow.getInstance().canvasRedraw(controller.getBounds());
			EditorWindow.getInstance().canvasRedraw(oldBounds);
		}
	}

	@Override
	public boolean isFocusControl() {
		boolean result = spX.isFocusControl() || spY.isFocusControl();
		if (sizeModifiable)
			result |= spW.isFocusControl() || spY.isFocusControl();
		return result;
	}
}
