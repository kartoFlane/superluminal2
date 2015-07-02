package com.kartoflane.common.swt.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;

import com.kartoflane.common.swt.graphics.HSV;


/**
 * A widget that allows the user to select a hue.
 * 
 * @author kartoFlane
 *
 */
public class HuePicker extends Composite {

	int triangleSize;

	Color selectionFillColor;
	Color selectionBorderColor;
	Canvas canvas;

	float selectedHue = 1.0f;


	public HuePicker( Composite parent, int style ) {
		super( parent, style );

		triangleSize = 8;
		selectionFillColor = new Color( getDisplay(), 255, 255, 255 );
		selectionBorderColor = new Color( getDisplay(), 128, 128, 128 );

		GridLayout gridLayout = new GridLayout( 1, false );
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = triangleSize;
		gridLayout.marginHeight = triangleSize / 2;
		gridLayout.horizontalSpacing = 0;
		setLayout( gridLayout );

		canvas = new Canvas( this, SWT.DOUBLE_BUFFERED | SWT.NO_FOCUS | SWT.BORDER );
		canvas.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );

		canvas.addPaintListener( new PaintListener() {
			@Override
			public void paintControl( PaintEvent e ) {
				paintHues( e );
			}
		} );

		addPaintListener( new PaintListener() {
			@Override
			public void paintControl( PaintEvent e ) {
				paintSelectionIndicator( e );
			}
		} );

		final MouseMoveListener mml = new MouseMoveListener() {
			@Override
			public void mouseMove( MouseEvent e ) {
				if ( ( e.stateMask & SWT.BUTTON1 ) != 0 ) {
					canvas.forceFocus();
					final float height = canvas.getClientArea().height;
					e.y = (int)Math.max( 0, Math.min( height, e.y ) );
					setSelection( 1 - ( e.y / height ) );
				}
			}
		};
		canvas.addMouseMoveListener( mml );
		canvas.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDown( MouseEvent e ) {
				e.stateMask = SWT.BUTTON1;
				mml.mouseMove( e );
			}
		} );
	}

	@Override
	public Point computeSize( int wHint, int hHint, boolean changed ) {
		Point p = super.computeSize( wHint, hHint, changed );

		p.x += triangleSize * 2;
		p.y += triangleSize / 2;

		return p;
	}

	public void setSelection( float hue ) {
		if ( hue < 0 || hue > 1.0f )
			throw new IllegalArgumentException( "0 < " + hue + " < 1.0" );

		selectedHue = hue;
		redraw();
		notifyListeners( SWT.Selection, new Event() );
	}

	public void setSelection( RGB rgb ) {
		if ( rgb == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		HSV hsv = new HSV( rgb );
		setSelection( hsv.h );
	}

	public void setSelection( Color color ) {
		if ( color == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		HSV hsv = new HSV( color.getRGB() );
		setSelection( hsv.h );
	}

	public float getSelection() {
		return selectedHue;
	}

	/**
	 * Returns the current selection in the form of a color, using the display associated with this control as device.
	 * Colors in SWT are a system resource, and as such have to be disposed when no longer used.
	 */
	public Color getSelectedColor() {
		return getSelectedColor( getDisplay() );
	}

	/**
	 * Returns the current selection in the form of a color.
	 * Colors in SWT are a system resource, and as such have to be disposed when no longer used.
	 */
	public Color getSelectedColor( Device d ) {
		return new HSV( selectedHue, 1.0f, 1.0f ).toColor( d );
	}

	public void addSelectionListener( SelectionListener listener ) {
		addListener( SWT.Selection, new TypedListener( listener ) );
	}

	public void removeListener( SelectionListener listener ) {
		removeListener( SWT.Selection, listener );
	}

	void paintHues( PaintEvent e ) {
		final Display d = getDisplay();
		final int width = canvas.getSize().x;
		final int height = canvas.getClientArea().height;
		final float stepY = 1f / height;

		HSV hsv = new HSV( 1, 1, 1 );

		for ( int y = 0; y < height; ++y ) {
			Color c = hsv.toColor( d );
			e.gc.setBackground( c );
			e.gc.fillRectangle( 0, y, width, 1 );
			c.dispose();

			hsv.h -= stepY;
		}
	}

	void paintSelectionIndicator( PaintEvent e ) {
		final int width = canvas.getSize().x;
		final int height = canvas.getClientArea().height;

		final int hOffset = (int)( ( 1 - selectedHue ) * height );

		int[] leftTriangle = new int[] {
				0, hOffset - 2 + triangleSize / 2,
				0, hOffset + 6 + triangleSize / 2,
				triangleSize, hOffset + triangleSize / 2 + 2
		};
		int[] rightTriangle = new int[] {
				width + triangleSize * 2 - 1, hOffset - 2 + triangleSize / 2,
				width + triangleSize * 2 - 1, hOffset + 6 + triangleSize / 2,
				width + triangleSize - 1, hOffset + triangleSize / 2 + 2
		};

		e.gc.setBackground( selectionFillColor );
		e.gc.fillPolygon( leftTriangle );
		e.gc.fillPolygon( rightTriangle );
		e.gc.setBackground( selectionBorderColor );
		e.gc.drawPolygon( leftTriangle );
		e.gc.drawPolygon( rightTriangle );
	}

	public void dispose() {
		canvas.dispose();
		selectionFillColor.dispose();
		selectionBorderColor.dispose();
		super.dispose();
	}
}
