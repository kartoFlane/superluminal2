package com.kartoflane.common.swt.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;

import com.kartoflane.common.swt.graphics.HSV;


/**
 * A widget that allows the user to select a shade of a specified hue.
 * 
 * @author kartoFlane
 *
 */
public class ShadePicker extends Composite {

	int selHalfSize;
	float currentHue;
	Color selectionBorderColor;
	Canvas canvas;

	float selectedSaturation = 0.0f;
	float selectedBrightness = 1.0f;


	public ShadePicker( Composite parent, int style ) {
		super( parent, style );

		selHalfSize = 5;
		currentHue = 1.0f;
		selectionBorderColor = new Color( getDisplay(), 128, 128, 128 );

		canvas = new Canvas( this, SWT.BORDER | SWT.NO_FOCUS | SWT.DOUBLE_BUFFERED );

		canvas.addPaintListener( new PaintListener() {
			@Override
			public void paintControl( PaintEvent e ) {
				paintShades( e );
				paintSelectionIndicator( e );
			}
		} );

		final MouseMoveListener mml = new MouseMoveListener() {
			@Override
			public void mouseMove( MouseEvent e ) {
				if ( ( e.stateMask & SWT.BUTTON1 ) != 0 ) {
					canvas.forceFocus();
					final float width = canvas.getClientArea().width;
					final float height = canvas.getClientArea().height;
					e.x = (int)Math.max( 0, Math.min( width, e.x ) );
					e.y = (int)Math.max( 0, Math.min( height, e.y ) );
					setSelection( e.x / width, 1 - ( e.y / height ) );
				}
			}
		};
		canvas.addMouseMoveListener( mml );
		canvas.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDown( MouseEvent e ) {
				if ( e.button == 1 ) {
					e.stateMask = SWT.BUTTON1;
					mml.mouseMove( e );
				}
			}
		} );
	}

	@Override
	public void setSize( int w, int h ) {
		super.setSize( w, h );
		canvas.setSize( w, h );
	}

	@Override
	public void setBounds( int x, int y, int w, int h ) {
		super.setBounds( x, y, w, h );
		canvas.setBounds( x, y, w - selHalfSize, h - selHalfSize );
	}

	public void setHue( float hue ) {
		if ( hue < 0 || hue > 1.0f )
			throw new IllegalArgumentException( "0 < " + hue + " < 1.0" );
		currentHue = hue;
		canvas.redraw();
	}

	public void setHue( RGB rgb ) {
		if ( rgb == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		HSV hsv = new HSV( rgb );
		setHue( hsv.h );
	}

	public void setHue( Color color ) {
		if ( color == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		HSV hsv = new HSV( color.getRGB() );
		setHue( hsv.h );
	}

	public void setSelection( float saturation, float brightness ) {
		if ( saturation < 0 || saturation > 1.0f )
			throw new IllegalArgumentException( "Saturation: 0 < " + saturation + " < 1.0" );
		if ( brightness < 0 || brightness > 1.0f )
			throw new IllegalArgumentException( "Brightness: 0 < " + brightness + " < 1.0" );

		selectedSaturation = saturation;
		selectedBrightness = brightness;
		canvas.redraw();
		notifyListeners( SWT.Selection, new Event() );
	}

	public void setSelection( RGB rgb ) {
		if ( rgb == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		HSV hsv = new HSV( rgb );
		setSelection( hsv.s, hsv.v );
	}

	public void setSelection( Color color ) {
		if ( color == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		HSV hsv = new HSV( color.getRGB() );
		setSelection( hsv.s, hsv.v );
	}

	public HSV getSelection() {
		return new HSV( currentHue, selectedSaturation, selectedBrightness );
	}

	public void addSelectionListener( SelectionListener listener ) {
		addListener( SWT.Selection, new TypedListener( listener ) );
	}

	public void removeListener( SelectionListener listener ) {
		removeListener( SWT.Selection, listener );
	}

	void paintShades( PaintEvent e ) {
		final Display d = getDisplay();
		final int width = canvas.getClientArea().width;
		final int height = canvas.getClientArea().height;
		final float stepY = 1f / height;

		final HSV white = new HSV( 0, 0, 1 );
		final HSV shade = new HSV( currentHue, 1.0f, 1.0f );

		try {
			for ( int y = 0; y < height; ++y ) {
				Color c1 = white.toColor( d );
				Color c2 = shade.toColor( d );
				e.gc.setForeground( c1 );
				e.gc.setBackground( c2 );
				e.gc.fillGradientRectangle( 0, y, width, 1, false );
				c1.dispose();
				c2.dispose();

				white.v -= stepY;
				shade.v -= stepY;
			}
		}
		catch ( Exception ex ) {
			System.err.printf( "%s, %s%n", white, shade );
		}
	}

	void paintSelectionIndicator( PaintEvent e ) {
		final int width = canvas.getClientArea().width;
		final int height = canvas.getClientArea().height;

		final int s = (int)( selectedSaturation * width );
		final int b = (int)( ( 1 - selectedBrightness ) * height );

		float d = (float)( Math.sqrt( Math.pow( 1 - selectedBrightness, 2 ) + Math.pow( selectedSaturation, 2 ) ) );
		d = d > 0.4 ? 1 : 0;
		HSV hsv = new HSV( 0, 0, d );
		if ( selectionBorderColor != null )
			selectionBorderColor.dispose();
		selectionBorderColor = hsv.toColor( getDisplay() );

		e.gc.setForeground( selectionBorderColor );
		e.gc.drawOval( s - selHalfSize, b - selHalfSize, selHalfSize * 2, selHalfSize * 2 );
	}

	public void dispose() {
		selectionBorderColor.dispose();
		canvas.dispose();
		super.dispose();
	}
}
