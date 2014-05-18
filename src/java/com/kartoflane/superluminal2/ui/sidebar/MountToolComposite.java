package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.tools.MountTool;
import com.kartoflane.superluminal2.ui.DirectionCombo;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;

public class MountToolComposite extends Composite implements DataComposite {

	private MountTool tool = null;
	private Label lblHelp;
	private Button btnRotated;
	private Label lblRotHelp;
	private Button btnMirrored;
	private Label lblMirHelp;
	private Label lblDirection;
	private Label lblDirHelp;
	private DirectionCombo cmbDirection;

	public MountToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setLayout(new GridLayout(2, false));

		tool = (MountTool) Manager.getSelectedTool();
		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");

		Label label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		label.setText("Weapon Mount Creation Tool");

		lblHelp = new Label(this, SWT.NONE);
		lblHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHelp.setImage(helpImage);
		StringBuilder buf = new StringBuilder();
		buf.append("- Left-clicking places the mount.\n");
		buf.append("- The ship can have 8 mounts in total.\n");
		buf.append("- Right-clicking rotates the mount.\n");
		buf.append("- Holding down Shift while right-clicking changes\n");
		buf.append("  the mount's direction.\n");
		buf.append("- Holding down Alt while right-clicking mirrors\n");
		buf.append("  the mount along X or Y axis.");
		lblHelp.setToolTipText(buf.toString());

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		btnRotated = new Button(this, SWT.CHECK);
		btnRotated.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRotated.setText("Rotated");
		btnRotated.setSelection(tool.isRotated());

		lblRotHelp = new Label(this, SWT.NONE);
		lblRotHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRotHelp.setImage(helpImage);
		lblRotHelp.setToolTipText("This determines the direction the weapon is going to face,\nand in which it's going to shoot.");

		btnRotated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setRotated(btnRotated.getSelection());
			}
		});

		btnMirrored = new Button(this, SWT.CHECK);
		btnMirrored.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnMirrored.setText("Mirrored");
		btnMirrored.setSelection(tool.isMirrored());

		lblMirHelp = new Label(this, SWT.NONE);
		lblMirHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMirHelp.setImage(helpImage);
		lblMirHelp.setToolTipText("Flips the weapon along X or Y axis, depending on Rotation.");

		btnMirrored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setMirrored(btnMirrored.getSelection());
			}
		});

		lblDirection = new Label(this, SWT.NONE);
		lblDirection.setText("Power-up Direction:");

		lblDirHelp = new Label(this, SWT.NONE);
		lblDirHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirHelp.setImage(helpImage);
		lblDirHelp.setToolTipText("This determines the direction in which the weapon\nwill 'slide' when it is powered up.");

		cmbDirection = new DirectionCombo(this, SWT.READ_ONLY, true);
		GridData gd_cmbDirection = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
		gd_cmbDirection.widthHint = 80;
		cmbDirection.setLayoutData(gd_cmbDirection);
		cmbDirection.select(DirectionCombo.toIndex(tool.getDirection()));

		cmbDirection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setDirection(cmbDirection.getDirection());
			}
		});

		pack();
	}

	public void updateData() {
		btnRotated.setSelection(tool.isRotated());
		btnMirrored.setSelection(tool.isMirrored());
		cmbDirection.select(tool.getDirection());
	}

	public void setController(AbstractController c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose() {
		super.dispose();
		Cache.checkInImage(this, "cpath:/assets/help.png");
	}
}
