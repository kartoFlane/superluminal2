package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.Hotkey;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.AugmentObject;


public class AugmentSearchDialog extends AbstractSearchDialog<AugmentObject>
{
	private static final int defaultWidth = 400;

	private Text txtBlueprint;
	private Text txtTitle;
	private Text txtDesc;
	private Button btnCase;


	public AugmentSearchDialog( Shell parent )
	{
		super( parent );
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createContents( Shell parent )
	{
		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL );
		shell.setLayout( new GridLayout( 2, false ) );
		shell.setText( Superluminal.APP_NAME + " - Augment Search" );

		Group grpContain = new Group( shell, SWT.NONE );
		grpContain.setLayout( new GridLayout( 2, false ) );
		grpContain.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
		grpContain.setText( "Containing Text In..." );

		Label lblBlueprint = new Label( grpContain, SWT.NONE );
		lblBlueprint.setText( "Blueprint Name:" );

		txtBlueprint = new Text( grpContain, SWT.BORDER );
		txtBlueprint.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		Label lblTitle = new Label( grpContain, SWT.NONE );
		lblTitle.setText( "Title:" );

		txtTitle = new Text( grpContain, SWT.BORDER );
		txtTitle.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		Label lblDescription = new Label( grpContain, SWT.NONE );
		lblDescription.setText( "Description:" );

		txtDesc = new Text( grpContain, SWT.BORDER );
		txtDesc.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		btnCase = new Button( grpContain, SWT.CHECK );
		btnCase.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );
		btnCase.setText( "Case-Sensitive" );

		Composite compButtons = new Composite( shell, SWT.NONE );
		GridLayout gl_compButtons = new GridLayout( 3, false );
		gl_compButtons.marginWidth = 0;
		gl_compButtons.marginHeight = 0;
		compButtons.setLayout( gl_compButtons );
		compButtons.setLayoutData( new GridData( SWT.FILL, SWT.BOTTOM, true, true, 2, 1 ) );

		Button btnDefault = new Button( compButtons, SWT.NONE );
		GridData gd_btnDefault = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_btnDefault.widthHint = 80;
		btnDefault.setLayoutData( gd_btnDefault );
		btnDefault.setText( "Default" );

		Button btnConfirm = new Button( compButtons, SWT.NONE );
		GridData gd_btnConfirm = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData( gd_btnConfirm );
		btnConfirm.setText( "Confirm" );

		Button btnCancel = new Button( compButtons, SWT.NONE );
		GridData gd_btnCancel = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 );
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData( gd_btnCancel );
		btnCancel.setText( "Cancel" );

		btnDefault.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					setResultDefault();
					dispose();
				}
			}
		);

		btnConfirm.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					setResultCurrent();
					dispose();
				}
			}
		);

		btnCancel.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					setResultUnchanged();
					dispose();
				}
			}
		);

		shell.addListener(
			SWT.Close, new Listener() {
				@Override
				public void handleEvent( Event e )
				{
					setResultUnchanged();
					dispose();
					e.doit = false;
				}
			}
		);

		shell.pack();
		shell.setSize( defaultWidth, shell.getSize().y );
		Point s = shell.getSize();
		Point p = parent.getSize();
		shell.setMinimumSize( s );
		shell.setLocation( parent.getLocation().x + p.x / 2 - s.x / 2, parent.getLocation().y + p.y / 2 - s.y / 2 );

		// Register hotkeys
		Hotkey h = new Hotkey();
		h.setKey( SWT.CR );
		h.addNotifyAction( btnConfirm, true );
		Manager.hookHotkey( shell, h );
	}

	@Override
	protected Predicate<AugmentObject> getFilter()
	{
		return new Predicate<AugmentObject>() {
			private boolean caseSensitive = AugmentSearchDialog.this.btnCase.getSelection();
			private String blue = AugmentSearchDialog.this.txtBlueprint.getText();
			private String title = AugmentSearchDialog.this.txtTitle.getText();
			private String desc = AugmentSearchDialog.this.txtDesc.getText();


			public boolean accept( AugmentObject o )
			{
				boolean result = true;

				if ( caseSensitive ) {
					result &= o.getBlueprintName().contains( blue );
					result &= o.getTitle().toString().contains( title );
					result &= o.getDescription().toString().contains( desc );
				}
				else {
					result &= o.getBlueprintName().toLowerCase().contains( blue.toLowerCase() );
					result &= o.getTitle().toString().toLowerCase().contains( title.toLowerCase() );
					result &= o.getDescription().toString().toLowerCase().contains( desc.toLowerCase() );
				}

				return result;
			}
		};
	}
}
