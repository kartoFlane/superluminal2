package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Redrawable;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.LayeredPainter;
import com.kartoflane.superluminal2.core.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.ObjectController;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.utils.Utils;


public abstract class BaseView
	implements View, Disposable, Redrawable
{
	public static final RGB SELECT_RGB = new RGB( 0, 0, 255 );
	public static final RGB HIGHLIGHT_RGB = new RGB( 0, 128, 192 );
	public static final RGB DENY_RGB = new RGB( 230, 100, 100 );
	public static final RGB ALLOW_RGB = new RGB( 50, 230, 50 );
	public static final RGB PIN_RGB = new RGB( 255, 255, 64 );
	public static final RGB PIN_HIGHLIGHT_RGB = new RGB( 192, 192, 128 );

	protected AbstractController controller = null;
	protected BaseModel model = null;

	protected Layers layer = null;
	protected Transform transform = null;

	protected String imagePath = null;
	protected Image image = null;
	protected Rectangle cachedImageBounds = null;
	protected Color borderColor = null;
	protected Color backgroundColor = null;
	protected int borderThickness = 0;

	protected RGB defaultBorder = null;
	protected RGB defaultBackground = null;

	protected float rotation = 0;
	protected int alpha = 255;

	protected boolean flipX = false;
	protected boolean flipY = false;
	protected boolean visible = true;
	protected boolean highlighted = false;

	private boolean disposed = false;


	public BaseView()
	{
	}

	public void setController( ObjectController controller )
	{
		this.controller = controller;
	}

	public final Layers getLayerId()
	{
		return layer;
	}

	@Override
	public void setController( Controller controller )
	{
		this.controller = (AbstractController)controller;
	}

	@Override
	public void setModel( Model model )
	{
		this.model = (BaseModel)model;
	}

	/** In degrees, 0 = north */
	public void setRotation( float rotation )
	{
		this.rotation = rotation;
	}

	/** In degrees, 0 = north */
	public float getRotation()
	{
		return rotation;
	}

	public void setVisible( boolean vis )
	{
		visible = vis;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setAlpha( int alpha )
	{
		if ( alpha < 0 || alpha > 255 )
			throw new IllegalArgumentException( "Argument is not within allowed range: " + alpha );
		this.alpha = alpha;
	}

	public int getAlpha()
	{
		return alpha;
	}

	public void setFlippedX( boolean flip )
	{
		flipX = flip;
	}

	public void setFlippedY( boolean flip )
	{
		flipY = flip;
	}

	public boolean isFlippedX()
	{
		return flipX;
	}

	public boolean isFlippedY()
	{
		return flipY;
	}

	public void setHighlighted( boolean high )
	{
		this.highlighted = high;
	}

	public boolean isHighlighted()
	{
		return highlighted;
	}

	public void setImage( String path )
	{
		if ( imagePath != null && path != null && imagePath.equals( path ) )
			return; // don't do anything if it's the same thing

		if ( image != null ) {
			Cache.checkInImage( this, imagePath );
		}

		imagePath = null;
		image = null;

		if ( path != null ) {
			image = Cache.checkOutImage( this, path );
			if ( image != null )
				cachedImageBounds = image.getBounds();
			imagePath = path;
		}
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public Rectangle getImageBounds()
	{
		return image == null ? new Rectangle( 0, 0, 0, 0 ) : Utils.copy( cachedImageBounds );
	}

	public void setBorderColor( RGB rgb )
	{
		if ( borderColor != null )
			Cache.checkInColor( this, borderColor.getRGB() );

		borderColor = null;

		if ( rgb != null )
			borderColor = Cache.checkOutColor( this, rgb );
	}

	public void setBorderColor( int red, int green, int blue )
	{
		if ( borderColor != null )
			Cache.checkInColor( this, borderColor.getRGB() );

		RGB rgb = new RGB( red, green, blue );
		borderColor = Cache.checkOutColor( this, rgb );
	}

	public RGB getBorderRGB()
	{
		return borderColor == null ? null : borderColor.getRGB();
	}

	public void setDefaultBorderColor( RGB rgb )
	{
		defaultBorder = rgb;
	}

	public void setDefaultBorderColor( int red, int green, int blue )
	{
		defaultBorder = new RGB( red, green, blue );
	}

	public RGB getDefaultBorderRGB()
	{
		return defaultBorder == null ? null : Utils.copy( defaultBorder );
	}

	public void setBackgroundColor( RGB rgb )
	{
		if ( backgroundColor != null )
			Cache.checkInColor( this, backgroundColor.getRGB() );

		backgroundColor = null;

		if ( rgb != null )
			backgroundColor = Cache.checkOutColor( this, rgb );
	}

	public void setBackgroundColor( int red, int green, int blue )
	{
		if ( backgroundColor != null )
			Cache.checkInColor( this, backgroundColor.getRGB() );

		RGB rgb = new RGB( red, green, blue );
		backgroundColor = Cache.checkOutColor( this, rgb );
	}

	public RGB getBackgroundRGB()
	{
		return backgroundColor == null ? null : backgroundColor.getRGB();
	}

	public void setDefaultBackgroundColor( RGB rgb )
	{
		defaultBackground = rgb;
	}

	public void setDefaultBackgroundColor( int red, int green, int blue )
	{
		defaultBackground = new RGB( red, green, blue );
	}

	public RGB getDefaultBackgroundRGB()
	{
		return defaultBackground == null ? null : Utils.copy( defaultBackground );
	}

	public void setBorderThickness( int borderThickness )
	{
		this.borderThickness = borderThickness;
	}

	public int getBorderThickness()
	{
		return borderThickness;
	}

	@Override
	public void redraw( PaintEvent e )
	{
		if ( visible ) {
			boolean trans = getRotation() % 360 != 0 || flipX || flipY;
			if ( trans ) {
				e.gc.setAdvanced( true );
				transform = new Transform( e.gc.getDevice() );
				Point p = model.getLocation();
				transform.translate( p.x, p.y );
				transform.rotate( getRotation() );
				transform.scale( flipX ? -1 : 1, flipY ? -1 : 1 );
				transform.translate( -p.x, -p.y );

				e.gc.setTransform( transform );
			}

			paintControl( e );

			if ( trans ) {
				e.gc.setTransform( null );

				transform.dispose();
				transform = null;

				e.gc.setAdvanced( false );
			}
		}
	}

	public void paintControl( PaintEvent e )
	{
	}

	/*
	 * ====================================================================================
	 * XXX: Paint image methods
	 * ====================================================================================
	 */

	/**
	 * Paints the image in the given area, modifying the image to fit.
	 */
	protected void paintImageResize( PaintEvent e, Image image, Rectangle cachedBounds, int x, int y, int w, int h, int alpha )
	{
		if ( image != null ) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha( alpha );

			e.gc.drawImage( image, 0, 0, cachedBounds.width, cachedBounds.height, x, y, w, h );

			e.gc.setAlpha( prevAlpha );
		}
	}

	/**
	 * Paints the image in the given area, modifying the image to fit.
	 */
	protected void paintImageResize( PaintEvent e, Image image, Rectangle cachedBounds, Rectangle rect, int alpha )
	{
		if ( image != null ) {
			paintImageResize( e, image, cachedBounds, rect.x, rect.y, rect.width, rect.height, alpha );
		}
	}

	/**
	 * Paints the image without any modifications at the center of the View.
	 */
	protected void paintImage( PaintEvent e, Image image, Rectangle cachedBounds, int alpha )
	{
		if ( image != null ) {
			paintImageResize(
				e, image, cachedBounds, model.getX() - cachedBounds.width / 2, model.getY() - cachedBounds.height / 2,
				cachedBounds.width, cachedBounds.height, alpha
			);
		}
	}

	/**
	 * Paints the image without any modifications, centered at the given location (relative to the canvas)
	 */
	protected void paintImage( PaintEvent e, Image image, Rectangle cachedBounds, int x, int y, int alpha )
	{
		if ( image != null ) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha( alpha );

			e.gc.drawImage(
				image, 0, 0, cachedBounds.width, cachedBounds.height, x - cachedBounds.width / 2,
				y - cachedBounds.height / 2, cachedBounds.width, cachedBounds.height
			);

			e.gc.setAlpha( prevAlpha );
		}
	}

	/**
	 * Paints part of the image without any modifications, centered at the given location (relative to the canvas).
	 * 
	 * @param srcX
	 *            x coordinate in the source image to copy from
	 * @param srcY
	 *            y coordinate in the source image to copy from
	 * @param srcW
	 *            width of the area that will be copied
	 * @param srcH
	 *            height of the area that will be copied
	 * @param x
	 *            x coordinate of the destination location at which the image will be drawn
	 * @param y
	 *            y coordinate of the destination location at which the image will be drawn
	 * @param w
	 *            width of the destination area at which the image will be drawn
	 * @param h
	 *            height of the destination area at which the image will be drawn
	 */
	protected void paintImageResize( PaintEvent e, Image image, int srcX, int srcY, int srcW, int srcH, int x, int y, int w, int h, int alpha )
	{
		if ( image != null ) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha( alpha );

			e.gc.drawImage( image, srcX, srcY, srcW, srcH, x - w / 2, y - h / 2, w, h );

			e.gc.setAlpha( prevAlpha );
		}
	}

	/**
	 * Paints the image without any modifications, with the image's top left corner at the given location.
	 */
	protected void paintImageCorner( PaintEvent e, Image image, Rectangle cachedBounds, int x, int y, int alpha )
	{
		if ( image != null ) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha( alpha );

			e.gc.drawImage( image, 0, 0, cachedBounds.width, cachedBounds.height, x, y, cachedBounds.width, cachedBounds.height );

			e.gc.setAlpha( prevAlpha );
		}
	}

	/**
	 * Paints part of the image without any modifications, with the image's top left corner at the given location.
	 * 
	 * @param srcX
	 *            x coordinate in the source image to copy from
	 * @param srcY
	 *            y coordinate in the source image to copy from
	 * @param srcW
	 *            width of the area that will be copied
	 * @param srcH
	 *            height of the area that will be copied
	 * @param x
	 *            x coordinate of the destination location at which the image will be drawn
	 * @param y
	 *            y coordinate of the destination location at which the image will be drawn
	 * @param w
	 *            width of the destination area at which the image will be drawn
	 * @param h
	 *            height of the destination area at which the image will be drawn
	 */
	protected void paintImageCorner( PaintEvent e, Image image, Rectangle cachedBounds, int srcX, int srcY, int srcW, int srcH, int x, int y, int w,
		int h, int alpha )
	{
		if ( image != null ) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha( alpha );

			srcW = Math.min( srcW, cachedBounds.width );
			srcH = Math.min( srcH, cachedBounds.height );

			e.gc.drawImage( image, srcX, srcY, srcW, srcH, x, y, w, h );

			e.gc.setAlpha( prevAlpha );
		}
	}

	/*
	 * ====================================================================================
	 * XXX: Paint background methods
	 * ====================================================================================
	 */

	protected void paintBackgroundSquare( PaintEvent e, Color backgroundColor, int alpha )
	{
		if ( backgroundColor != null ) {
			paintBackgroundSquare(
				e, model.getX() - model.getW() / 2, model.getY() - model.getH() / 2,
				model.getW(), model.getH(), backgroundColor, alpha
			);
		}
	}

	protected void paintBackgroundSquare( PaintEvent e, Rectangle rect, Color backgroundColor, int alpha )
	{
		if ( backgroundColor != null ) {
			paintBackgroundSquare( e, rect.x, rect.y, rect.width, rect.height, backgroundColor, alpha );
		}
	}

	protected void paintBackgroundSquare( PaintEvent e, int x, int y, int w, int h, Color backgroundColor, int alpha )
	{
		if ( backgroundColor != null ) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();

			e.gc.setBackground( backgroundColor );
			e.gc.setAlpha( alpha );

			e.gc.fillRectangle( x, y, w, h );

			e.gc.setBackground( prevBgColor );
			e.gc.setAlpha( prevAlpha );
		}
	}

	protected void paintBackgroundOval( PaintEvent e, Color backgroundColor, int alpha )
	{
		if ( backgroundColor != null ) {
			paintBackgroundOval(
				e, model.getX() - model.getW() / 2, model.getY() - model.getH() / 2,
				model.getW(), model.getH(), backgroundColor, alpha
			);
		}
	}

	protected void paintBackgroundOval( PaintEvent e, Rectangle rect, Color backgroundColor, int alpha )
	{
		if ( backgroundColor != null ) {
			paintBackgroundOval( e, rect.x, rect.y, rect.width, rect.height, backgroundColor, alpha );
		}
	}

	protected void paintBackgroundOval( PaintEvent e, int x, int y, int w, int h, Color backgroundColor, int alpha )
	{
		if ( backgroundColor != null ) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();

			e.gc.setBackground( backgroundColor );
			e.gc.setAlpha( alpha );

			e.gc.fillOval( x, y, w, h );

			e.gc.setBackground( prevBgColor );
			e.gc.setAlpha( prevAlpha );
		}
	}

	protected void paintBackgroundPolygon( PaintEvent e, int[] polygon, Color backgroundColor, int alpha )
	{
		if ( backgroundColor != null ) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();

			e.gc.setBackground( backgroundColor );
			e.gc.setAlpha( alpha );

			e.gc.fillPolygon( polygon );

			e.gc.setBackground( prevBgColor );
			e.gc.setAlpha( prevAlpha );
		}
	}

	/*
	 * ====================================================================================
	 * XXX: Paint border methods
	 * ====================================================================================
	 */

	protected void paintBorderSquare( PaintEvent e, Color borderColor, int borderThickness, int alpha )
	{
		paintBorderSquare(
			e, model.getX() - model.getW() / 2, model.getY() - model.getH() / 2,
			model.getW(), model.getH(), borderColor, borderThickness, alpha
		);
	}

	protected void paintBorderSquare( PaintEvent e, Rectangle rect, Color borderColor, int borderThickness, int alpha )
	{
		paintBorderSquare( e, rect.x, rect.y, rect.width, rect.height, borderColor, borderThickness, alpha );
	}

	protected void paintBorderSquare( PaintEvent e, int x, int y, int w, int h, Color borderColor, int borderThickness, int alpha )
	{
		if ( borderColor != null ) {
			Color prevFgColor = e.gc.getForeground();
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();

			e.gc.setForeground( borderColor );
			e.gc.setAlpha( alpha );
			e.gc.setLineWidth( borderThickness );

			// Lines are drawn from the center, which makes the math a little funky
			e.gc.drawRectangle(
				x + borderThickness / 2, y + borderThickness / 2,
				w - 1 - borderThickness / 2, h - 1 - borderThickness / 2
			);

			e.gc.setForeground( prevFgColor );
			e.gc.setAlpha( prevAlpha );
			e.gc.setLineWidth( prevWidth );
		}
	}

	protected void paintBorderOval( PaintEvent e, Color borderColor, int borderThickness, int alpha )
	{
		paintBorderOval(
			e, model.getX() - model.getW() / 2, model.getY() - model.getH() / 2,
			model.getW(), model.getH(), borderColor, borderThickness, alpha
		);
	}

	protected void paintBorderOval( PaintEvent e, Rectangle rect, Color borderColor, int borderThickness, int alpha )
	{
		paintBorderOval( e, rect.x, rect.y, rect.width, rect.height, borderColor, borderThickness, alpha );
	}

	protected void paintBorderOval( PaintEvent e, int x, int y, int w, int h, Color borderColor, int borderThickness, int alpha )
	{
		if ( borderColor != null ) {
			Color prevFgColor = e.gc.getForeground();
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();

			e.gc.setForeground( borderColor );
			e.gc.setAlpha( alpha );
			e.gc.setLineWidth( borderThickness );

			// Lines are drawn from the center, which makes the math a little funky
			e.gc.drawOval(
				x + borderThickness / 2, y + borderThickness / 2,
				w - 1 - borderThickness / 2, h - 1 - borderThickness / 2
			);

			e.gc.setForeground( prevFgColor );
			e.gc.setAlpha( prevAlpha );
			e.gc.setLineWidth( prevWidth );
		}
	}

	protected void paintBorderPolygon( PaintEvent e, int[] polygon, Color borderColor, int borderThickness, int alpha )
	{
		if ( borderColor != null ) {
			Color prevFgColor = e.gc.getForeground();
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();

			e.gc.setForeground( borderColor );
			e.gc.setAlpha( alpha );
			e.gc.setLineWidth( borderThickness );

			e.gc.drawPolygon( polygon );

			e.gc.setForeground( prevFgColor );
			e.gc.setAlpha( prevAlpha );
			e.gc.setLineWidth( prevWidth );
		}
	}

	/*
	 * ====================================================================================
	 * XXX: Paint line methods
	 * ====================================================================================
	 */

	/**
	 * Line is always drawn from <tt>(rect.x, rect.y)</tt> to <tt>(rect.x + rect.width, rect.y + rect.height)</tt>.
	 * The rect can have negative width or height, however.
	 */
	protected void paintLine( PaintEvent e, Rectangle rect, Color borderColor, int borderThickness, int alpha )
	{
		paintLine( e, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, borderColor, borderThickness, alpha );
	}

	protected void paintLine( PaintEvent e, int sx, int sy, int ex, int ey, Color borderColor, int borderThickness, int alpha )
	{
		if ( borderColor != null ) {
			Color prevFgColor = e.gc.getForeground();
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();

			e.gc.setForeground( borderColor );
			e.gc.setAlpha( alpha );
			e.gc.setLineWidth( borderThickness );

			e.gc.drawLine( sx, sy, ex, ey );

			e.gc.setForeground( prevFgColor );
			e.gc.setAlpha( prevAlpha );
			e.gc.setLineWidth( prevWidth );
		}
	}

	/**
	 * Registers this view with the LayeredPainter object.
	 * 
	 * @param layer
	 *            Id of the layer to which the view will be added
	 */
	public void addToPainter( Layers layer )
	{
		LayeredPainter painter = LayeredPainter.getInstance();
		if ( layer == null || !painter.getLayers().contains( layer ) )
			throw new IllegalArgumentException( "Illegal layer." );

		this.layer = layer;
		painter.add( controller, layer );
	}

	public void addToPainterBottom( Layers layer )
	{
		LayeredPainter painter = LayeredPainter.getInstance();
		if ( layer == null || !painter.getLayers().contains( layer ) )
			throw new IllegalArgumentException( "Illegal layer." );

		this.layer = layer;
		painter.addToBottom( controller, layer );
	}

	/** Unregisters this view from the LayeredPainter object */
	public void removeFromPainter()
	{
		LayeredPainter.getInstance().remove( controller );
	}

	/** Unregisters this view from the specfied layer */
	public void removeFromPainter( Layers layer )
	{
		LayeredPainter.getInstance().remove( controller, layer );
	}

	public void dispose()
	{
		removeFromPainter();
		setVisible( false );

		if ( borderColor != null )
			Cache.checkInColor( this, borderColor.getRGB() );
		if ( backgroundColor != null )
			Cache.checkInColor( this, backgroundColor.getRGB() );
		if ( image != null )
			Cache.checkInImage( this, imagePath );

		borderColor = null;
		backgroundColor = null;
		image = null;
		imagePath = null;

		disposed = true;
	}

	public boolean isDisposed()
	{
		return disposed;
	}
}
