package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.views.PropView;

public class PropController extends AbstractController {

	private PropController(AbstractController parent, BaseModel model, PropView view) {
		super();
		setModel(model);
		setView(view);

		setSelectable(false);
		setLocModifiable(false);
		setBounded(false);
		setCollidable(false);
		setParent(parent);
	}

	/**
	 * @param parent
	 *            the parent of this controller which it will follow
	 */
	public static PropController newInstance(AbstractController parent) {
		BaseModel model = new BaseModel();
		PropView view = new PropView();
		PropController controller = new PropController(parent, model, view);

		return controller;
	}

	public void setLayer(Layers layer) {
		view.removeFromPainter();
		view.addToPainter(layer);
	}

	public void setRotation(float rad) {
		view.setRotation(rad);
	}

	public float getRotation() {
		return view.getRotation();
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
}
