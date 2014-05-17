package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.Polygon;
import com.kartoflane.superluminal2.components.enums.Shapes;
import com.kartoflane.superluminal2.components.interfaces.Identifiable;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.views.PropView;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.PropDataComposite;

public abstract class PropController extends AbstractController implements Identifiable {

	protected final String identifier;
	protected Shapes shape;
	protected Polygon polygon;
	protected String compositeTitle = "Prop";

	private PropController(AbstractController parent, BaseModel model, PropView view, String id) {
		super();
		setModel(model);
		setView(view);
		setShape(Shapes.SQUARE);

		identifier = id;

		setSelectable(false);
		setLocModifiable(false);
		setBounded(false);
		setCollidable(false);
		setParent(parent);
	}

	/**
	 * @param parent
	 *            the parent of this controller which it will follow
	 * @param id
	 *            the string used to identify and distinguish this prop
	 */
	public PropController(AbstractController parent, String id) {
		this(parent, new BaseModel(), new PropView(), id);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setCompositeTitle(String title) {
		compositeTitle = title;
	}

	public String getCompositeTitle() {
		return compositeTitle;
	}

	public void setShape(Shapes shape) {
		this.shape = shape;
		updateView();
	}

	public Shapes getShape() {
		return shape;
	}

	public void setPolygon(Polygon poly) {
		polygon = poly;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setLayer(Layers layer) {
		view.removeFromPainter();
		view.addToPainter(layer);
	}

	public void setFlippedX(boolean flip) {
		view.setFlippedX(flip);
	}

	public boolean isFlippedX() {
		return view.isFlippedX();
	}

	public void setFlippedY(boolean flip) {
		view.setFlippedY(flip);
	}

	public boolean isFlippedY() {
		return view.isFlippedY();
	}

	public void setAlpha(int alpha) {
		view.setAlpha(alpha);
	}

	public int getAlpha() {
		return view.getAlpha();
	}

	public void setBorderThickness(int b) {
		view.setBorderThickness(b);
	}

	public int getBorderThickness() {
		return view.getBorderThickness();
	}

	public void setImage(String path) {
		view.setImage(path);
		Rectangle b = view.getImageBounds();
		setSize(b.width, b.height);
	}

	public String getImage() {
		return view.getImagePath();
	}

	public void setBorderColor(RGB rgb) {
		view.setBorderColor(rgb);
	}

	public void setBorderColor(int r, int g, int b) {
		view.setBorderColor(r, g, b);
	}

	public RGB getBorderRGB() {
		return view.getBorderRGB();
	}

	public void setBackgroundColor(RGB rgb) {
		view.setBackgroundColor(rgb);
	}

	public void setBackgroundColor(int r, int g, int b) {
		view.setBackgroundColor(r, g, b);
	}

	public RGB getBackgroundRGB() {
		return view.getBackgroundRGB();
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new PropDataComposite(parent, this);
	}
}
