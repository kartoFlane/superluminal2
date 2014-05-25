package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.interfaces.Alias;

public class ImageObject extends GameObject implements Alias {

	private String imagePath = null;
	private String alias = null;

	public ImageObject() {
		setDeletable(false);
	}

	public void update() {
		// Nothing to do here -- can't interpret the data in any meaningful way at this level
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
