package com.kartoflane.superluminal2.ftl;

public class AnimObject {

	// no need to make fields private if we're going to fully expose them anyway *shrug* // TODO maybe not? :/
	public final String animName;
	public String filePath;
	public int spriteW = 0;
	public int spriteH = 0;
	public int frameW = 0;
	public int frameH = 0;

	public AnimObject(String animName) {
		this.animName = animName;
	}
}
