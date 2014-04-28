package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.interfaces.Alias;

public class ImageObject extends GameObject implements Alias {

	private static final long serialVersionUID = 5829085341187861553L;

	private String imagePath = null;
	private String alias = null;

	public ImageObject() {
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
