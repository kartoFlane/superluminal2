package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.props.PropController;


public class PropDataComposite extends Composite implements DataComposite
{
	private PropController controller = null;
	private Label label;


	public PropDataComposite( Composite parent, PropController controller )
	{
		super( parent, SWT.NONE );
		this.controller = controller;

		createContents();
	}

	protected void createContents()
	{
		setLayout( new GridLayout( 4, false ) );

		label = new Label( this, SWT.NONE );
		label.setAlignment( SWT.CENTER );
		label.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );
		label.setText( controller.getCompositeTitle() );

		Label separator = new Label( this, SWT.SEPARATOR | SWT.HORIZONTAL );
		separator.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );
	}

	public void updateData()
	{
		label.setText( controller.getCompositeTitle() );
	}

	@Override
	public void setController( AbstractController controller )
	{
		this.controller = (PropController)controller;
	}

	public void reloadController()
	{
	}
}
