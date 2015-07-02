package com.kartoflane.common.swt.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.common.swt.graphics.HSV;
import com.kartoflane.common.swt.ui.widgets.HuePicker;
import com.kartoflane.common.swt.ui.widgets.ShadePicker;


/**
 * This dialog allows the user to select a color of their liking.
 * 
 * @author kartoFlane
 *
 */
public class SquareColorPickerDialog extends Dialog {

	private static final int shadeWidth = 270;
	private static final int shadeHeight = 270;
	private static final int hueWidth = 25;

	private RGB result = null;
	private RGB current = null;

	private Shell shell;
	private ShadePicker shadePicker;
	private HuePicker huePicker;
	private Composite coButtons;
	private Button btnConfirm;
	private Button btnCancel;
	private Composite coControls;
	private Composite coColors;
	private Label lblNew;
	private Label lblCurrent;
	private Label lblColorNew;
	private Label lblColorCurrent;
	private Composite coColorsCompare;
	private Text txtR;
	private Text txtG;
	private Text txtB;
	private Text txtH;
	private Text txtS;
	private Text txtV;
	private Label lblHex;
	private Text txtHex;
	private Label lblSeparator;

	private Text[] txtFields = new Text[7];
	private VerifyListener vl;
	private ModifyListener ml;


	public SquareColorPickerDialog( Shell parent ) {
		super( parent );
		setText( "Color Picker" );
	}

	/**
	 * Opens the color picker dialog using the specified color as the "current" color.
	 * Returns the selected color, or null if the user canceled selection.
	 * 
	 * When this method returns, the dialog is already disposed.
	 */
	public RGB open( RGB current ) {
		Shell parent = getParent();

		if ( current == null )
			this.current = new RGB( 0, 0, 0 );
		result = this.current;

		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL );
		shell.setText( getText() );
		shell.setLayout( new GridLayout( 3, false ) );

		createWidgets();
		shell.pack();

		Point size = shell.getSize();
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation( parLoc.x + parSize.x / 2 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2 );

		shell.open();
		Display display = parent.getDisplay();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() )
				display.sleep();
		}
		return result;
	}

	private void createWidgets() {
		shadePicker = new ShadePicker( shell, SWT.NONE );
		GridData gd_spShade = new GridData( SWT.LEFT, SWT.TOP, false, false, 1, 1 );
		gd_spShade.heightHint = shadeHeight;
		gd_spShade.widthHint = shadeWidth;
		shadePicker.setLayoutData( gd_spShade );
		shadePicker.setHue( current );
		shadePicker.setSelection( current );

		shadePicker.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				updateColorSelection();
			}
		} );

		huePicker = new HuePicker( shell, SWT.NONE );
		GridData gd_hpHue = new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 );
		gd_hpHue.heightHint = shadeHeight;
		gd_hpHue.widthHint = hueWidth;
		huePicker.setLayoutData( gd_hpHue );
		huePicker.setSelection( current );

		huePicker.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				shadePicker.setHue( huePicker.getSelectedColor() );
				updateColorSelection();
			}
		} );

		coControls = new Composite( shell, SWT.NONE );
		coControls.setLayout( new GridLayout( 5, false ) );
		coControls.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 1, 1 ) );

		coColors = new Composite( coControls, SWT.NONE );
		coColors.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 5, 1 ) );
		GridLayout gl_coColors = new GridLayout( 3, false );
		gl_coColors.marginWidth = 0;
		gl_coColors.marginHeight = 0;
		coColors.setLayout( gl_coColors );

		lblNew = new Label( coColors, SWT.NONE );
		lblNew.setText( "new" );

		coColorsCompare = new Composite( coColors, SWT.BORDER );
		coColorsCompare.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		coColorsCompare.setBounds( 0, 0, 64, 64 );
		GridLayout gl_coColorsCompare = new GridLayout( 2, false );
		gl_coColorsCompare.horizontalSpacing = 0;
		gl_coColorsCompare.verticalSpacing = 0;
		gl_coColorsCompare.marginWidth = 0;
		gl_coColorsCompare.marginHeight = 0;
		coColorsCompare.setLayout( gl_coColorsCompare );

		lblColorNew = new Label( coColorsCompare, SWT.NONE );
		GridData gd_lblColorNew = new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 );
		gd_lblColorNew.heightHint = 25;
		gd_lblColorNew.widthHint = 50;
		lblColorNew.setLayoutData( gd_lblColorNew );
		lblColorNew.setSize( 50, 20 );

		lblColorCurrent = new Label( coColorsCompare, SWT.NONE );
		GridData gd_lblColorCurrent = new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 );
		gd_lblColorCurrent.heightHint = 25;
		gd_lblColorCurrent.widthHint = 50;
		lblColorCurrent.setLayoutData( gd_lblColorCurrent );
		lblColorCurrent.setSize( 50, 20 );
		lblColorCurrent.setBackground( new Color( getParent().getDisplay(), current ) );

		lblCurrent = new Label( coColors, SWT.NONE );
		lblCurrent.setText( "current" );
		lblCurrent.setBounds( 0, 0, 22, 15 );

		lblSeparator = new Label( coControls, SWT.NONE );
		lblSeparator.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 5, 1 ) );

		Label lblR = new Label( coControls, SWT.NONE );
		lblR.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		lblR.setText( "R:" );

		txtR = new Text( coControls, SWT.BORDER );
		txtR.setText( "0" );
		GridData gd_txtR = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_txtR.widthHint = 40;
		txtR.setLayoutData( gd_txtR );

		Label lblH = new Label( coControls, SWT.NONE );
		lblH.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 ) );
		lblH.setText( "H:" );

		txtH = new Text( coControls, SWT.BORDER );
		txtH.setText( "0" );
		GridData gd_txtH = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 );
		gd_txtH.widthHint = 40;
		txtH.setLayoutData( gd_txtH );

		Label lblUnitH = new Label( coControls, SWT.NONE );
		lblUnitH.setText( "°" );

		Label lblG = new Label( coControls, SWT.NONE );
		lblG.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		lblG.setText( "G:" );

		txtG = new Text( coControls, SWT.BORDER );
		txtG.setText( "0" );
		GridData gd_txtG = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_txtG.widthHint = 40;
		txtG.setLayoutData( gd_txtG );

		Label lblS = new Label( coControls, SWT.NONE );
		lblS.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		lblS.setText( "S:" );

		txtS = new Text( coControls, SWT.BORDER );
		txtS.setText( "0" );
		GridData gd_txtS = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 );
		gd_txtS.widthHint = 40;
		txtS.setLayoutData( gd_txtS );

		Label lblUnitS = new Label( coControls, SWT.NONE );
		lblUnitS.setText( "%" );

		Label lblB = new Label( coControls, SWT.NONE );
		lblB.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		lblB.setText( "B:" );

		txtB = new Text( coControls, SWT.BORDER );
		txtB.setText( "0" );
		GridData gd_txtB = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_txtB.widthHint = 40;
		txtB.setLayoutData( gd_txtB );

		Label lblV = new Label( coControls, SWT.NONE );
		lblV.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		lblV.setText( "B:" );

		txtV = new Text( coControls, SWT.BORDER );
		txtV.setText( "0" );
		GridData gd_txtV = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 );
		gd_txtV.widthHint = 40;
		txtV.setLayoutData( gd_txtV );

		Label lblUnitV = new Label( coControls, SWT.NONE );
		lblUnitV.setText( "%" );

		lblHex = new Label( coControls, SWT.NONE );
		lblHex.setLayoutData( new GridData( SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1 ) );
		lblHex.setText( "#" );

		txtHex = new Text( coControls, SWT.BORDER );
		txtHex.setText( "000000" );
		txtHex.setLayoutData( new GridData( SWT.FILL, SWT.BOTTOM, true, true, 4, 1 ) );

		txtFields[0] = txtHex;
		txtFields[1] = txtR;
		txtFields[2] = txtG;
		txtFields[3] = txtB;
		txtFields[4] = txtH;
		txtFields[5] = txtS;
		txtFields[6] = txtV;

		coButtons = new Composite( shell, SWT.NONE );
		GridLayout gl_coButtons = new GridLayout( 2, false );
		gl_coButtons.marginWidth = 0;
		gl_coButtons.marginHeight = 0;
		coButtons.setLayout( gl_coButtons );
		coButtons.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

		btnConfirm = new Button( coButtons, SWT.NONE );
		GridData gd_btnConfirm = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData( gd_btnConfirm );
		btnConfirm.setText( "Confirm" );

		btnCancel = new Button( coButtons, SWT.NONE );
		GridData gd_btnCancel = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 );
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData( gd_btnCancel );
		btnCancel.setText( "Cancel" );

		coControls.setTabList( new Control[] { txtR, txtG, txtB, txtH, txtS, txtV, txtHex } );
		shell.setTabList( new Control[] { coControls, coButtons } );

		lblColorCurrent.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDown( MouseEvent e ) {
				if ( e.button == 1 ) {
					huePicker.setSelection( current );
					shadePicker.setSelection( current );
					updateColorSelection();
				}
			}
		} );

		btnConfirm.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				dispose();
			}
		} );

		btnCancel.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				result = null;
				dispose();
			}
		} );

		shell.addListener( SWT.Close, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				e.doit = false;
				result = null;
				dispose();
			}
		} );

		vl = new VerifyListener() {
			@Override
			public void verifyText( VerifyEvent e ) {
				e.doit = verifyInput( e.getSource(), e.character );
			}
		};

		ml = new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {
				setInput( e.getSource() );
			}
		};

		FocusListener fl = new FocusAdapter() {
			@Override
			public void focusGained( FocusEvent e ) {
				if ( e.getSource() == txtHex ) {
					String t = txtHex.getText();
					if ( t.length() % 2 == 1 )
						txtHex.setText( t + "0" );
				}
				updateColorSelection();
			}
		};
		txtHex.addFocusListener( fl );
		txtR.addFocusListener( fl );
		txtG.addFocusListener( fl );
		txtB.addFocusListener( fl );
		txtH.addFocusListener( fl );
		txtS.addFocusListener( fl );
		txtV.addFocusListener( fl );

		updateColorSelection();
	}

	private void updateColorSelection() {
		// Updating the txt fields while listeners are registered results in a
		// huge amount of lag. Remove the listeners temporarily, and re-register
		// them when we're done.
		removeListeners();

		HSV hsv = shadePicker.getSelection();
		result = hsv.toRGB();
		lblColorNew.getBackground().dispose();
		lblColorNew.setBackground( new Color( getParent().getDisplay(), result ) );

		if ( !txtR.isFocusControl() )
			txtR.setText( "" + result.red );
		if ( !txtG.isFocusControl() )
			txtG.setText( "" + result.green );
		if ( !txtB.isFocusControl() )
			txtB.setText( "" + result.blue );

		DecimalFormatSymbols symbols = new DecimalFormatSymbols( Locale.ENGLISH );
		NumberFormat formatter = new DecimalFormat( "#0.00", symbols );
		hsv.h = huePicker.getSelection();

		if ( !txtH.isFocusControl() )
			txtH.setText( formatter.format( hsv.h * 360 ) );
		if ( !txtS.isFocusControl() )
			txtS.setText( formatter.format( hsv.s * 100 ) );
		if ( !txtV.isFocusControl() )
			txtV.setText( formatter.format( hsv.v * 100 ) );

		if ( !txtHex.isFocusControl() ) {
			String hr = Integer.toHexString( result.red );
			hr = hr.length() == 0 ? "00" : ( hr.length() == 1 ? "0" + hr : hr );
			String hg = Integer.toHexString( result.green );
			hg = hg.length() == 0 ? "00" : ( hg.length() == 1 ? "0" + hg : hg );
			String hb = Integer.toHexString( result.blue );
			hb = hb.length() == 0 ? "00" : ( hb.length() == 1 ? "0" + hb : hb );
			txtHex.setText( hr + hg + hb );
		}

		addListeners();
	}

	private void removeListeners() {
		for ( Text t : txtFields ) {
			t.removeVerifyListener( vl );
			t.removeModifyListener( ml );
		}
	}

	private void addListeners() {
		for ( Text t : txtFields ) {
			t.addVerifyListener( vl );
			t.addModifyListener( ml );
		}
	}

	private boolean verifyInput( Object src, char ch ) {
		if ( ch == '\b' || ch == 127 ) // Backspace and delete keys
			return true;

		Text t = (Text)src;
		if ( src == txtHex ) {
			return ( t.getText().length() < 6 || t.getSelectionCount() > 0 ) &&
					( ( ch >= '0' && ch <= '9' ) || ( ch >= 'a' && ch <= 'f' ) ||
					( ch >= 'A' && ch <= 'F' ) );
		}
		else if ( src == txtR || src == txtG || src == txtB ) {
			return ( t.getText().length() < 3 || t.getSelectionCount() > 0 ) && ( ch >= '0' && ch <= '9' );
		}
		else if ( src == txtH || src == txtS || src == txtV ) {
			if ( ch == '.' && ( t.getText().indexOf( "." ) == -1 || t.getSelectionText().contains( "." ) ) )
				return true;
			return ( t.getText().length() < 6 || t.getSelectionCount() > 0 ) && ( ch >= '0' && ch <= '9' );
		}
		return false;
	}

	private void setInput( Object src ) {
		if ( src == txtHex ) {
			String text = ( (Text)src ).getText();
			RGB rgb = new RGB( 0, 0, 0 );
			try {
				rgb.red = Integer.parseInt( text.substring( 0, 2 ), 16 );
				rgb.green = Integer.parseInt( text.substring( 2, 4 ), 16 );
				rgb.blue = Integer.parseInt( text.substring( 4, 6 ), 16 );
			}
			catch ( StringIndexOutOfBoundsException e ) {
			}
			HSV hsv = new HSV( rgb );
			huePicker.setSelection( hsv.h );
			shadePicker.setSelection( hsv.s, hsv.v );
		}
		else if ( src == txtR || src == txtG || src == txtB ) {
			RGB rgb = new RGB( 0, 0, 0 );
			try {
				rgb.red = Math.min( 255, Integer.parseInt( txtR.getText() ) );
			}
			catch ( NumberFormatException e ) {
			}
			try {
				rgb.green = Math.min( 255, Integer.parseInt( txtG.getText() ) );
			}
			catch ( NumberFormatException e ) {
			}
			try {
				rgb.blue = Math.min( 255, Integer.parseInt( txtB.getText() ) );
			}
			catch ( NumberFormatException e ) {
			}
			HSV hsv = new HSV( rgb );
			huePicker.setSelection( hsv.h );
			shadePicker.setSelection( hsv.s, hsv.v );
		}
		else if ( src == txtH || src == txtS || src == txtV ) {
			HSV hsv = new HSV( 0, 0, 0 );
			try {
				hsv.h = Math.min( 360, Float.parseFloat( txtH.getText() ) ) / 360f;
			}
			catch ( NumberFormatException e ) {
			}
			try {
				hsv.s = Math.min( 100, Float.parseFloat( txtS.getText() ) ) / 100f;
			}
			catch ( NumberFormatException e ) {
			}
			try {
				hsv.v = Math.min( 100, Float.parseFloat( txtV.getText() ) ) / 100f;
			}
			catch ( NumberFormatException e ) {
			}

			if ( src == txtH ) {
				huePicker.setSelection( hsv.h );
				shadePicker.setSelection( hsv.s, hsv.v );
			}
			else if ( src == txtS ) {
				shadePicker.setSelection( hsv.s, shadePicker.getSelection().v );
			}
			else if ( src == txtV ) {
				shadePicker.setSelection( shadePicker.getSelection().s, hsv.v );
			}
		}
	}

	public void dispose() {
		lblColorCurrent.getBackground().dispose();
		shell.dispose();
	}
}
