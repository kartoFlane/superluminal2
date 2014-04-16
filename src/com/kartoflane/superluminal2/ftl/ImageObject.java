package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.interfaces.Alias;

public class ImageObject extends GameObject implements Alias {

	private static final long serialVersionUID = 5829085341187861553L;

	private String imagePath = null;
	private int locX = 0;
	private int locY = 0;

	private String alias = null;

	public ImageObject() {
	}

	public void setLocation(int x, int y) {
		locX = x;
		locY = y;
	}

	public void setX(int x) {
		locX = x;
	}

	public void setY(int y) {
		locY = y;
	}

	public int getX() {
		return locX;
	}

	public int getY() {
		return locY;
	}

	public void setImagePath(String path) {
		imagePath = path;
	}

	public String getImagePath() {
		return imagePath;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}
}
